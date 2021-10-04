package com.seckill.dis.gateway.seckill;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.seckill.dis.common.api.cache.RedisServiceApi;
import com.seckill.dis.common.api.cache.vo.GoodsKeyPrefix;
import com.seckill.dis.common.api.cache.vo.OrderKeyPrefix;
import com.seckill.dis.common.api.cache.vo.SkKeyPrefix;
import com.seckill.dis.common.api.goods.GoodsServiceApi;
import com.seckill.dis.common.api.goods.vo.GoodsVo;
import com.seckill.dis.common.api.mq.MqProviderApi;
import com.seckill.dis.common.api.mq.vo.SkMessage;
import com.seckill.dis.common.api.order.OrderServiceApi;
import com.seckill.dis.common.api.seckill.SeckillServiceApi;
import com.seckill.dis.common.api.seckill.vo.VerifyCodeVo;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.common.domain.SeckillOrder;
import com.seckill.dis.common.result.CodeMsg;
import com.seckill.dis.common.result.Result;
import com.seckill.dis.common.util.MD5Util;
import com.seckill.dis.common.util.UUIDUtil;
import com.seckill.dis.common.util.VerifyCodeUtil;
import com.seckill.dis.gateway.aop_log.LogAnnotation;
import com.seckill.dis.gateway.config.access.AccessLimit;
import com.seckill.dis.gateway.exception.MyBlockHandler;

import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 秒杀接口
 * @author mata
 */
@Controller
@RequestMapping("/seckill/")
public class SeckillController implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(SeckillController.class);

    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;

    @Reference(interfaceClass = GoodsServiceApi.class)
    GoodsServiceApi goodsService;

    @Reference(interfaceClass = SeckillServiceApi.class)
    SeckillServiceApi seckillService;

    @Reference(interfaceClass = OrderServiceApi.class)
    OrderServiceApi orderService;

    @Reference(interfaceClass = MqProviderApi.class)
    MqProviderApi sender;

   // 用于内存标记，标记库存是否为空，从而减少对redis的访问
    private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 获取秒杀接口地址
     * 1. 每一次点击秒杀，都会生成一个随机的秒杀地址返回给客户端
     * 2. 对秒杀的次数做限制（通过自定义拦截器注解完成）
     * @param user
     * @param goodsId    秒杀的商品id
     * @param verifyCode 验证码
     * @return 被隐藏的秒杀接口路径
     */
    @LogAnnotation(module = "获取秒杀接口地址",operation = "获取秒杀接口地址")
    @AccessLimit(seconds = 5, maxAccessCount = 5, needLogin = true)
    @RequestMapping(value = "path", method = RequestMethod.GET)
    @ResponseBody
    @SentinelResource(value = "Seckill",blockHandler ="blockHandlerFordoSeckill",blockHandlerClass = {MyBlockHandler.class})
    public Result<String> getSeckillPath( UserVo user, @RequestParam("goodsId") long goodsId, @RequestParam(value = "verifyCode") int verifyCode) {

        /** 在执行下面的逻辑之前，会先对path请求进行拦截处理（@AccessLimit， AccessInterceptor），防止访问次数过于频繁，对服务器造成过大的压力 */
        boolean check_goodsID=redisService.exists(GoodsKeyPrefix.seckillGoodsInf, ""+goodsId);
        if(!check_goodsID) return Result.error(CodeMsg.SECKILL_DOODS_ILLEGAL);

        long  startTime = redisService.get(GoodsKeyPrefix.seckillGoodsInf, ""+goodsId ,GoodsVo.class).getStartDate().getTime();
        if(startTime >new Date().getTime()){
            return Result.error(CodeMsg.SECKILL_TIME_ILLEGAL);
        }
        // 校验验证码
        boolean check = this.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) return Result.error(CodeMsg.VERITF_FAIL);// 检验不通过，请求非法
        // 检验通过，获取秒杀路径
        String path = this.createSkPath(user, goodsId);
        // 向客户端回传随机生成的秒杀地址
        return Result.success(path);
    }

    /* 压力测试时候注释掉 :验证码和秒杀地址隐藏 */
    /**
     * 秒杀逻辑（页面静态化分离，不需要直接将页面返回给客户端，而是返回客户端需要的页面动态数据，返回数据时json格式）
     * GET/POST的@RequestMapping是有区别的
     * 通过随机的path，客户端隐藏秒杀接口
     * 优化: 不同于每次都去数据库中读取秒杀订单信息，而是在第一次生成秒杀订单成功后，
     * 将订单存储在redis中，再次读取订单信息的时候就直接从redis中读取
     * @param user
     * @param goodsId
     * @param path    隐藏的秒杀地址，为客户端回传的path，最初也是有服务端产生的
     * @return 订单详情或错误码
     */
    // {path}为客户端回传的path，最初也是有服务端产生的
    @LogAnnotation(module = "秒杀逻辑",operation = "秒杀逻辑")
    @RequestMapping(value = "{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSeckill( UserVo user, @RequestParam("goodsId") long goodsId, @PathVariable("path") String path) {
        // 验证path是否正确
        boolean check = this.checkPath(user, goodsId, path);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);// 请求非法
        }
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 通过内存标记，减少对redis的访问，秒杀未结束才继续访问redis
        Boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        // 预减库存，同时在库存为0时标记该商品已经结束秒杀
        Long stock = redisService.decr(GoodsKeyPrefix.GOODS_STOCK, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);// 秒杀结束。标记该商品已经秒杀结束
            redisService.set(GoodsKeyPrefix.GOODS_STOCK, "" +goodsId, 0);
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        // 判断是否重复秒杀
        // 从redis中取缓存，减少数据库的访问
        SeckillOrder order = redisService.get(OrderKeyPrefix.SK_ORDER, ":" + user.getUuid() + "_" + goodsId, SeckillOrder.class);
        // 如果缓存中不存该数据，则从数据库中取
        if (order == null) {
            order = orderService.getSeckillOrderByUserIdAndGoodsId(user.getUuid(), goodsId);
        }
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        // 商品有库存且用户为秒杀商品，则将秒杀请求放入MQ
        SkMessage message = new SkMessage(user.getUuid(),goodsId);
        // 放入MQ(对秒杀请求异步处理，直接返回)
        sender.sendSkMessage(message);
        // 排队中
        return Result.success(0);
    }

    /**
     * 用于返回用户秒杀的结果
     *
     * @param model
     * @param user
     * @param goodsId
     * @return orderId：成功, -1：秒杀失败, 0： 排队中
     */
    @LogAnnotation(module = "用于返回用户秒杀的结果",operation = "用于返回用户秒杀的结果")
    @RequestMapping(value = "result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> getSeckillResult( UserVo user, @RequestParam("goodsId") long goodsId) {
        long result = seckillService.getSeckillResult(user.getUuid(), goodsId);
        if(result!=-1L) return Result.success(result);
        boolean isOver = localOverMap.get(goodsId);
        if(!isOver){
            return Result.success(0L);
        }
        return Result.success(-1L);
    }

    /**
     * goods_detail.htm: $("#verifyCodeImg").attr("src", "/seckill/verifyCode?goodsId=" + $("#goodsId").val());
     * 使用HttpServletResponse的输出流返回客户端异步获取的验证码（异步获取的代码如上所示）
     */
    @LogAnnotation(module = "验证码",operation = "验证码")
    @RequestMapping(value = "verifyCode", method = RequestMethod.GET)
    @ResponseBody
    @SentinelResource(value = "verifyCode",blockHandler ="blockHandlerForVerifyCodel",blockHandlerClass = {MyBlockHandler.class})
    public Result<String> getVerifyCode(HttpServletResponse response, UserVo user, @RequestParam("goodsId") long goodsId) {
        logger.info("获取验证码");
        if (user == null ) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check_goodsID=redisService.exists(GoodsKeyPrefix.seckillGoodsInf, ""+goodsId);
        if(!check_goodsID) return Result.error(CodeMsg.SECKILL_DOODS_ILLEGAL);

        // 刷新验证码的时候置缓存中的随机地址无效
        String path = redisService.get(SkKeyPrefix.SK_PATH, "" + user.getUuid() + "_" + goodsId, String.class);
        if (path != null)
            redisService.delete(SkKeyPrefix.SK_PATH, "" + user.getUuid() + "_" + goodsId);

        // 创建验证码
        try {
            // String verifyCodeJsonString = seckillService.createVerifyCode(user, goodsId);
            VerifyCodeVo verifyCode = VerifyCodeUtil.createVerifyCode();

            // 验证码结果预先存到redis中
            redisService.set(SkKeyPrefix.VERIFY_RESULT, user.getUuid() + "_" + goodsId, verifyCode.getExpResult());
            ServletOutputStream out = response.getOutputStream();
            // 将图片写入到resp对象中
            ImageIO.write(verifyCode.getImage(), "JPEG", out);
            out.close();
            out.flush();

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
    }

    /**
     * 调试接口，可以动态加载商品信息到redis缓存中
     */
    @RequestMapping(value = "test", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> test() {
        logger.info("商品信息热加载");
        afterPropertiesSet();
        return Result.success(true);     
    }

    // 检验检验码的计算结果
    private boolean checkVerifyCode(UserVo user, long goodsId, int verifyCode) {
        // 从redis中获取验证码计算结果
        Integer oldCode = redisService.get(SkKeyPrefix.VERIFY_RESULT, user.getUuid() + "_" + goodsId, Integer.class);
        // logger.info(verifyCode+" "+oldCode);
        if (oldCode == null || oldCode - verifyCode != 0) {
            return false;
        }
        // 如果校验成功，则说明校验码过期，删除校验码缓存，防止重复提交同一个验证码
        redisService.delete(SkKeyPrefix.VERIFY_RESULT, user.getUuid() + "_" + goodsId);
        return true;
    }

    // 创建秒杀地址, 并将其存储在redis中
    @LogAnnotation(module = "创建秒杀地址",operation = "创建秒杀地址")
    public String createSkPath(UserVo user, long goodsId) {
        // 随机生成秒杀地址
        String path = MD5Util.md5(UUIDUtil.uuid() + "123456");
        // 将随机生成的秒杀地址存储在redis中（保证不同的用户和不同商品的秒杀地址是不一样的）
        redisService.set(SkKeyPrefix.SK_PATH, "" + user.getUuid() + "_" + goodsId, path);
        return path;
    }

    // 验证路径是否正确
    public boolean checkPath(UserVo user, long goodsId, String path) {
        if (user == null || path == null)
            return false;
        // 从redis中读取出秒杀的path变量是否为本次秒杀操作执行前写入redis中的path
         logger.info(goodsId+"");
        String oldPath = redisService.get(SkKeyPrefix.SK_PATH, "" + user.getUuid() + "_" + goodsId, String.class);
         logger.info(oldPath+" "+path);
        return path.equals(oldPath);
    }

    /**
     * 服务器程序启动的时候加载商品列表信息
     */
    @Override
    public void afterPropertiesSet() {
        List<GoodsVo> goods = goodsService.listGoodsVo();
        if (goods == null) {
            return;
        }
        // 将商品的库存信息存储在redis中
        for (GoodsVo good : goods) {
            redisService.set(GoodsKeyPrefix.GOODS_STOCK, "" + good.getId(), good.getStockCount());
            redisService.set(GoodsKeyPrefix.seckillGoodsInf, ""+ good.getId() , good);
            // 在系统启动时，标记库存不为空
            localOverMap.put(good.getId(), false);
        }
    }
}
