package com.seckill.dis.order.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Mapper
public interface OrderInfoMapper {

    String findByOrderStatus(@Param("orderId") String orderId);

    void updateOrderStatus(@Param("orderId") String orderId);

    void saveOrderInfo(@Param("pkid") String pkid, @Param("orderId") String orderId, @Param("orderStatus") String orderStatus);
}

