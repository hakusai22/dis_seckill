package com.seckill.dis.gateway.order;

import com.seckill.dis.common.api.goods.GoodsServiceApi;
import com.seckill.dis.common.api.goods.vo.GoodsVo;
import com.seckill.dis.common.api.order.OrderServiceApi;
import com.seckill.dis.common.api.order.vo.OrderDetailVo;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.common.domain.OrderInfo;
import com.seckill.dis.common.result.CodeMsg;
import com.seckill.dis.common.result.Result;
import com.seckill.dis.gateway.aop_log.LogAnnotation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * 订单服务接口
 * @author xizizzz
 */


@Controller
@RequestMapping("/order/")
public class OrderController {

    @Reference(interfaceClass = OrderServiceApi.class)
    OrderServiceApi orderService;

    @Reference(interfaceClass = GoodsServiceApi.class)
    GoodsServiceApi goodsService;

    /**
     * 获取订单详情
     */
    @LogAnnotation(module = "获取订单详情",operation = "获取订单详情")
    @RequestMapping("detail")
    @ResponseBody
    public Result<OrderDetailVo> orderInfo(Model model, UserVo user, @RequestParam("orderId") long orderId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // 获取订单信息
        OrderInfo order = orderService.getOrderById(orderId);
        if (Objects.isNull(order)) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        // 如果订单存在，则根据订单信息获取商品信息
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setUser(user);// 设置用户信息
        vo.setOrder(order); // 设置订单信息
        vo.setGoods(goods); // 设置商品信息
        return Result.success(vo);
    }
}
