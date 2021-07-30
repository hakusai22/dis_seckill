package com.seckill.dis.common.api.cache.vo;


import java.io.Serializable;

/**
 * redis中，用于商品信息的key
 *
 * @author xizizzz
 */
public class GoodsKeyPrefix extends BaseKeyPrefix  implements Serializable {
    public GoodsKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    // 缓存在redis中的商品列表页面的key的前缀
    public static GoodsKeyPrefix goodsListKeyPrefix = new GoodsKeyPrefix(60, "goodsList");
    public static GoodsKeyPrefix GOODS_LIST_HTML = new GoodsKeyPrefix(60, "goodsListHtml");

    // 缓存在redis中的商品详情页面的key的前缀
    public static GoodsKeyPrefix goodsDetailKeyPrefix = new GoodsKeyPrefix(60, "goodsDetail");

    // 缓存在redis中的商品信息(缓存过期时间为永久)
    public static GoodsKeyPrefix seckillGoodsInf =new GoodsKeyPrefix(0, "goodsInf");
    /**
     * 缓存在redis中的商品库存的前缀(缓存过期时间为永久)
     */
    public static GoodsKeyPrefix GOODS_STOCK = new GoodsKeyPrefix(0, "goodsStock");
}
