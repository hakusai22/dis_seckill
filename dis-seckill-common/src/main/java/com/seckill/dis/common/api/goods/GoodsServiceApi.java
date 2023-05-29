package com.seckill.dis.common.api.goods;

import com.seckill.dis.common.api.goods.vo.GoodsVo;
import com.seckill.dis.common.api.goods.vo.InsertGoodsDTO;

import java.text.ParseException;
import java.util.List;

/**
 * 商品服务接口
 * @author hakusai
 */
public interface GoodsServiceApi {

    /**
     * 获取商品列表
     *
     * @return
     */
    List<GoodsVo> listGoodsVo() throws ParseException;

    /**
     * 通过商品的id查出商品的所有信息（包含该商品的秒杀信息）
     *
     * @param goodsId
     * @return
     */
    GoodsVo getGoodsVoByGoodsId(long goodsId);

    /**
     * order表减库存
     * @param goodsId
     */
    void reduceStock(long goodsId);

    /**
     * g
     * @param goodsId
     */
    void deleteGoods(long goodsId);

    /**
     * 删除商品
     * @param goodsId
     */
    void updateGoods(long goodsId);

    void insertGoods(InsertGoodsDTO insertGoodsDTO);
}
