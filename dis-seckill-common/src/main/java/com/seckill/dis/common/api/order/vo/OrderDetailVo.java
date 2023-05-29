package com.seckill.dis.common.api.order.vo;

import com.seckill.dis.common.api.goods.vo.GoodsVo;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.common.domain.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单详情，包含订单信息和商品信息
 * <p>
 * 用于将数据传递给客户端
 *
 * @author hakusai
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {

    /**
     * 用户信息
     */
    private UserVo user;

    /**
     * 商品信息
     */
    private GoodsVo goods;
    /**
     * 订单信息
     */
    private OrderInfo order;
}
