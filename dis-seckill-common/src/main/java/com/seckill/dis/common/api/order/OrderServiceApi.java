package com.seckill.dis.common.api.order;

import java.util.List;

import com.seckill.dis.common.domain.OrderInfo;
import com.seckill.dis.common.domain.SeckillOrder;

/**
 * 订单服务接口
 *
 * @author hakusai
 */
public interface OrderServiceApi {

    /**
     * 通过订单id获取订单
     *
     * @param orderId
     * @return
     */
    OrderInfo getOrderById(long orderId);

    /**
     * 通过用户id与商品id从订单列表中获取订单信息，这个地方用到了唯一索引（unique index!!!!!）
     *
     * @param userId
     * @param goodsId
     * @return 秒杀订单信息
     */
    SeckillOrder getSeckillOrderByUserIdAndGoodsId(long userId, long goodsId);

    /**
     * 创建订单
     *
     * @param userId
     */
    OrderInfo createOrder(Long userId, Long goodsdId);


    String findByOrderStatus(String orderId);

    void updateOrderStatus(String orderId);

    void saveOrderInfo(String pkid, String orderId, String orderStatus);

    void updateOrderById(long orderId);

    List<OrderInfo> getAllOrder();

    void deleteOrder(Long orderId);
}
