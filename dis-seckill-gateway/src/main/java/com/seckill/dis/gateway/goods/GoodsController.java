package com.seckill.dis.gateway.goods;

import com.seckill.dis.common.api.cache.RedisServiceApi;
import com.seckill.dis.common.api.cache.vo.GoodsKeyPrefix;
import com.seckill.dis.common.api.goods.GoodsServiceApi;
import com.seckill.dis.common.api.goods.vo.GoodsDetailVo;
import com.seckill.dis.common.api.goods.vo.GoodsVo;
import com.seckill.dis.common.api.goods.vo.InsertGoodsDTO;
import com.seckill.dis.common.api.user.UserServiceApi;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.common.domain.SeckillUser;
import com.seckill.dis.common.result.Result;
import com.seckill.dis.gateway.aop_log.LogAnnotation;
import com.seckill.dis.gateway.user.UserController;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

/**
 * 商品模块接口
 *
 * @author hakusai
 */

@Controller
@RequestMapping("/goods/")
public class GoodsController {

  private static Logger logger = LoggerFactory.getLogger(UserController.class);

  @Reference(interfaceClass = RedisServiceApi.class)
  RedisServiceApi redisService;

  @Reference(interfaceClass = GoodsServiceApi.class)
  GoodsServiceApi goodsService;

  @Reference(interfaceClass = UserServiceApi.class)
  UserServiceApi userService;

  /**
   * 因为在redis缓存中不存页面缓存时需要手动渲染，所以注入一个视图解析器，自定义渲染
   */
  @Autowired
  ThymeleafViewResolver thymeleafViewResolver;


  @GetMapping(value = "deleteGoods")
  @ResponseBody
  public String deleteGoods(@RequestParam("goodsId") long goodsId, Model model,
      HttpServletResponse response, HttpServletRequest request,
      UserVo user) throws ParseException {
    goodsService.deleteGoods(goodsId);

    List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
    model.addAttribute("goodsList", goodsVoList);
    model.addAttribute("user", user);
    // 3. 渲染html
    WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
    // (第一个参数为渲染的html文件名，第二个为web上下文：里面封装了web应用的上下文)
    String html = thymeleafViewResolver.getTemplateEngine().process("adminGoods_list", webContext);
    if (!StringUtils.isEmpty(html)) // 如果html文件不为空，则将页面缓存在redis中
      redisService.set(GoodsKeyPrefix.GOODS_LIST_HTML, "", html);

    return html;
  }

  /**
   * 添加秒杀商品
   *
   * @param user
   * @return
   * @throws ParseException
   */
  @PostMapping(value = "insertGoods")
  @ResponseBody
  public Result<String> insertGoods(InsertGoodsDTO insertGoodsDTO, Model model,
      HttpServletResponse response, HttpServletRequest request,
      UserVo user) throws ParseException {
    goodsService.insertGoods(insertGoodsDTO);
    return Result.success("添加成功");
  }


  @GetMapping(value = "updateGoods")
  @ResponseBody
  public String updateGoods(@RequestParam("goodsId") long goodsId, Model model,
      HttpServletResponse response, HttpServletRequest request,
      UserVo user) throws ParseException {
    goodsService.updateGoods(goodsId);

    List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
    model.addAttribute("goodsList", goodsVoList);
    model.addAttribute("user", user);
    // 3. 渲染html
    WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
    // (第一个参数为渲染的html文件名，第二个为web上下文：里面封装了web应用的上下文)
    String html = thymeleafViewResolver.getTemplateEngine().process("adminGoods_list", webContext);
    if (!StringUtils.isEmpty(html)) // 如果html文件不为空，则将页面缓存在redis中
      redisService.set(GoodsKeyPrefix.GOODS_LIST_HTML, "", html);
    return html;
  }

  /**
   * 获取 SKUser 对象，并将其传递到页面解析器
   * 从数据库中获取商品信息（包含秒杀信息）
   * QPS: 1267, 用户数目5000，每个用户发起10次请求，共5000*10次请求
   * 页面级缓存实现；从redis中取页面，如果没有则需要手动渲染页面，并且将渲染的页面存储在redis中供下一次访问时获取
   *
   * @param model 响应的资源文件
   * @param user  通过自定义参数解析器UserArgumentResolver解析的 SKUser 对象
   * @return
   */
  @RequestMapping(value = "goodsList", produces = "text/html")
// produces表明：这个请求会返回text/html媒体类型的数据
  @ResponseBody
  public String goodsList(HttpServletRequest request,
      HttpServletResponse response, Model model,
      UserVo user) throws ParseException {
    logger.info("获取商品列表");
    // 1. 从redis缓存中取html
    String html = redisService.get(GoodsKeyPrefix.GOODS_LIST_HTML, "goodsList", String.class);
    if (!StringUtils.isEmpty(html))
      return html;
    // 2. 如果redis中不存在该缓存，则需要手动渲染
    // 查询商品列表，用于手动渲染时将商品数据填充到页面
    List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
    System.out.println("goodsVoList" + goodsVoList);
    model.addAttribute("goodsList", goodsVoList);
    model.addAttribute("user", user);
    // 3. 渲染html
    WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
    // (第一个参数为渲染的html文件名，第二个为web上下文：里面封装了web应用的上下文)
    html = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);
    if (!StringUtils.isEmpty(html)) // 如果html文件不为空，则将页面缓存在redis中
      redisService.set(GoodsKeyPrefix.GOODS_LIST_HTML, "goodsList", html);

    return html;
  }

  @RequestMapping(value = "aminGoodsList", produces = "text/html")
// produces表明：这个请求会返回text/html媒体类型的数据
  @ResponseBody
  public String aminGoodsList(HttpServletRequest request,
      HttpServletResponse response, Model model,
      UserVo user) throws ParseException {
    // 1. 从redis缓存中取html
    String html = redisService.get(GoodsKeyPrefix.ADMIN_GOODS_LIST_HTML, "aminGoodsList", String.class);
    if (!StringUtils.isEmpty(html))
      return html;
    // 2. 如果redis中不存在该缓存，则需要手动渲染
    // 查询商品列表，用于手动渲染时将商品数据填充到页面
    List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
    model.addAttribute("goodsList", goodsVoList);
//        System.out.println("goodsVoList"+goodsVoList);

    model.addAttribute("user", user);
    // 3. 渲染html
    WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
    // (第一个参数为渲染的html文件名，第二个为web上下文：里面封装了web应用的上下文)
    html = thymeleafViewResolver.getTemplateEngine().process("adminGoods_list", webContext);
    if (!StringUtils.isEmpty(html)) // 如果html文件不为空，则将页面缓存在redis中
      redisService.set(GoodsKeyPrefix.ADMIN_GOODS_LIST_HTML, "aminGoodsList", html);
    return html;
  }

  /**
   * 处理商品详情页（页面静态化处理, 直接将数据返回给客户端，交给客户端处理）
   * URL级缓存实现；从redis中取商品详情页面，如果没有则需要手动渲染页面，并且将渲染的页面存储在redis中供下一次访问时获取
   * 实际上URL级缓存和页面级缓存是一样的，只不过URL级缓存会根据url的参数从redis中取不同的数据
   */
  @LogAnnotation(module = "商品详情页", operation = "商品详情页")
  @RequestMapping(value = "getDetails/{goodsId}")
  @ResponseBody
  public Result<GoodsDetailVo> getDetails(UserVo user,
      @PathVariable("goodsId") long goodsId) {
    logger.info("获取商品详情");
    // 通过商品id在数据库查询
    GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    // 获取商品的秒杀开始与结束的时间
    long startDate = goods.getStartDate().getTime();
    long endDate = goods.getEndDate().getTime();
    long now = System.currentTimeMillis();
    // 秒杀状态; 0: 秒杀未开始，1: 秒杀进行中，2: 秒杀已结束
    int skStatus = 0;
    // 秒杀剩余时间
    int remainSeconds = 0;
    if (now < startDate) { // 秒杀未开始
      skStatus = 0;
      remainSeconds = (int) ((startDate - now) / 1000);
    } else if (now > endDate) { // 秒杀已结束
      skStatus = 2;
      remainSeconds = -1;
    } else { // 秒杀进行中
      skStatus = 1;
      remainSeconds = 0;
    }
    // 服务端封装商品数据直接传递给客户端，而不用渲染页面
    GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
    goodsDetailVo.setGoods(goods);
    goodsDetailVo.setUser(user);
    goodsDetailVo.setRemainSeconds(remainSeconds);
    goodsDetailVo.setSeckillStatus(skStatus);
    return Result.success(goodsDetailVo);
  }

  @LogAnnotation(module = "秒杀用户页", operation = "秒杀用户页")
  @RequestMapping(value = "getAllUser")
  @ResponseBody
  public void getDetails(UserVo user, Model model) {
    List<SeckillUser> allUserInfo = userService.getAllUserInfo();
    model.addAttribute("allUserInfo", allUserInfo);
  }


}
