package com.seckill.dis.goods.persistence;

import com.seckill.dis.common.api.goods.vo.GoodsVo;
import com.seckill.dis.common.api.goods.vo.InsertGoodsDTO;
import com.seckill.dis.common.domain.SeckillGoods;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * goods 表的数据库访问层
 */
@Mapper
public interface GoodsMapper {

    /**
     * 查出商品信息（包含该商品的秒杀信息）
     * 利用左外连接(LEFT JOIN...ON...)的方式查
     * @return
     */
    @Select("SELECT g.*, mg.stock_count, mg.start_date, mg.end_date, mg.seckill_price FROM seckill_goods mg LEFT JOIN goods g ON mg.goods_id=g.id")
    List<GoodsVo> listGoodsVo();

    /**
     * 通过商品的id查出商品的所有信息（包含该商品的秒杀信息）
     * @param goodsId
     */
    @Select("SELECT g.*, mg.stock_count, mg.start_date, mg.end_date, mg.seckill_price FROM seckill_goods mg LEFT JOIN goods g ON mg.goods_id=g.id where g.id = #{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") Long goodsId);

    /**
     * 减少 seckill_order 中的库存
     * (删除,Redis实现分布式锁)
     */
    @Update("UPDATE seckill_goods SET stock_count = stock_count-1 WHERE goods_id=#{goodsId}")
    int reduceStack(@Param("goodsId") Long goodsId);

    @Delete("delete from seckill_goods WHERE id=#{goodsId}")
    int deleteGoods(@Param("goodsId") Long goodsId);

    @Update("UPDATE seckill_goods SET end_date =#{endDate} WHERE goods_id=#{goodsId}")
    int updateGoods(@Param("goodsId") Long goodsId,@Param("endDate") Date endDate);

    @Insert("INSERT INTO seckill_goods (goods_id, seckill_price, stock_count, start_date, end_date) VALUES (#{goodsId}, #{seckillPrice}, #{stockCount}, #{startDate}, #{endDate})")
    long insert(InsertGoodsDTO insertGoodsDTO);

}
