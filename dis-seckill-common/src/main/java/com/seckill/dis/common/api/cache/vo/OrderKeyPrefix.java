package com.seckill.dis.common.api.cache.vo;

import java.io.Serializable;

/**
 * 存储订单的key前缀
 *
 * @author hakusai
 */
public class OrderKeyPrefix extends BaseKeyPrefix  implements Serializable {

    public OrderKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public OrderKeyPrefix(String prefix) {
        super(prefix);
    }

    /**
     * 秒杀订单信息的前缀
     */
    public static OrderKeyPrefix getSeckillOrderByUidGid = new OrderKeyPrefix("getSeckillOrderByUidGid");
    public static OrderKeyPrefix SK_ORDER = new OrderKeyPrefix("SK_ORDER");


}
