package com.seckill.dis.order.persistence;

import com.seckill.dis.common.domain.OrderInfo;
import com.seckill.dis.common.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;

/**
 * seckill_order 表数据访问层
 * @author xizizzz
 */
@Mapper
public interface OrderMapper {

    /**
     * 通过用户id与商品id从订单列表中获取订单信息
     * @param userId  用户id
     * @param goodsId 商品id
     * @return 秒杀订单信息
     */
    @Select("SELECT * FROM seckill_order WHERE user_id=#{userId} AND goods_id=#{goodsId}")
    SeckillOrder getSeckillOrderByUserIdAndGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    /**
     * 将订单信息插入 order_info 表中
     * @param orderInfo 订单信息
     * @return 插入成功的订单信息id
     */
    @Insert("INSERT INTO order_info (id,user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date, delivery_addr_id)"
            + "VALUES (#{id},#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate},#{deliveryAddrId})")
    // 查询出插入订单信息的表id，并返回
//    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "SELECT last_insert_id()")
    long insert(OrderInfo orderInfo);

    /**
     * 将秒杀订单信息插入到 seckill_order 中
     * @param seckillOrder 秒杀订单
     */
    @Insert("INSERT INTO seckill_order(id,user_id, order_id, goods_id,create_time) VALUES (#{id},#{userId}, #{orderId}, #{goodsId}, #{createTime})")
    void insertSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
}
