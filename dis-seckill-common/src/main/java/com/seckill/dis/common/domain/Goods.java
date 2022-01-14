package com.seckill.dis.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * goods 表
 * @author xizizzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods {

    private Long id;

    private String goodsName;

    private String goodsTitle;

    private String goodsImg;

    private String goodsDetail;

    private Double goodsPrice;

    private Long goodsStock;

}
