package com.seckill.dis.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * seckill_goods
 * @author xizizzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillGoods implements Serializable{

    private Long id;

    private Long goodsId;

    private Double seckillPrice;

    private Integer stockCount;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date startDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date endDate;

}
