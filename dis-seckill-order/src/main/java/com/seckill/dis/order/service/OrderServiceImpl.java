package com.seckill.dis.order.service;

import com.seckill.dis.common.api.cache.RedisServiceApi;
import com.seckill.dis.common.api.cache.vo.GoodsKeyPrefix;
import com.seckill.dis.common.api.cache.vo.OrderKeyPrefix;
import com.seckill.dis.common.api.goods.vo.GoodsVo;
import com.seckill.dis.common.api.order.OrderServiceApi;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.common.domain.OrderInfo;
import com.seckill.dis.common.domain.SeckillOrder;
import com.seckill.dis.common.util.IdWorker;
import com.seckill.dis.order.persistence.OrderMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 订单服务实现
 * @author xizizzz
 */
@Service(interfaceClass = OrderServiceApi.class)
public class OrderServiceImpl implements OrderServiceApi {
    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    OrderMapper orderMapper;

    @Reference(interfaceClass = RedisServiceApi.class)
    RedisServiceApi redisService;

    @Override
    public OrderInfo getOrderById(long orderId) {
        return orderMapper.getOrderById(orderId);
    }

    @Override
    public SeckillOrder getSeckillOrderByUserIdAndGoodsId(long userId, long goodsId) {
        return orderMapper.getSeckillOrderByUserIdAndGoodsId(userId, goodsId);
    }

    /**
     * 创建订单
     * 首先向数据库中写入数据，然后将数据写到缓存中，这样可以保证缓存和数据库中的数据的一致
     * 1. 向 order_info 中插入订单详细信息
     * 2. 向 seckill_order 中插入订单概要
     * 两个操作需要构成一个数据库事务
     * @param userId
     * @return
     */
    @Transactional
    @Override
    public OrderInfo createOrder(Long userId, Long goodsId) {
        OrderInfo orderInfo = new OrderInfo();
        GoodsVo goods = redisService.get(GoodsKeyPrefix.seckillGoodsInf, ""+goodsId ,GoodsVo.class);

        long id1 = new IdWorker(1, 1, 1).nextId();
        orderInfo.setCreateDate(new Date()).setDeliveryAddrId(0L)
        .setGoodsCount(1)// 订单中商品的数量
        .setGoodsId(goods.getId())
        .setGoodsName(goods.getGoodsName())
        .setGoodsPrice(goods.getSeckillPrice())// 秒杀价格
        .setOrderChannel(1)
        .setStatus(0)
        .setUserId(userId)
        .setId(id1);
        System.out.println(orderInfo);

        // 将订单信息插入 order_info 表中
        long orderId = orderMapper.insert(orderInfo);
        logger.debug("将订单信息插入 order_info 表中: 记录为" + orderId);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId())
        .setOrderId(id1)
        .setUserId(userId);
        // 将秒杀订单插入 seckill_order 表中
        orderMapper.insertSeckillOrder(seckillOrder);
        logger.debug("将秒杀订单插入 seckill_order 表中");
        
        // 将秒杀订单概要信息存储于redis中
        redisService.set(OrderKeyPrefix.SK_ORDER, ":" + userId + "_" + goods.getId(), seckillOrder);
        return orderInfo;
    }
}
