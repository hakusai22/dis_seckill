package com.seckill.dis.common.api.goods.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seckill.dis.common.domain.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品信息（并且包含商品的秒杀信息）
 * 商品信息和商品的秒杀信息是存储在两个表中的（goods 和 seckill_goods）
 * 继承 Goods 便具有了 goods 表的信息，再额外添加上 seckill_goods 的信息即可
 *
 * @author hakusai
 */

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoodsVo extends Goods implements Serializable {

    private Double seckillPrice;

    private Integer stockCount;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date startDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date endDate;

    private String startDateString;

    private String endDateString;

}
