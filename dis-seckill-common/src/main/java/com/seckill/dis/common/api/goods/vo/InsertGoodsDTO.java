package com.seckill.dis.common.api.goods.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author yinpeng8
 * @Date 2022/1/14 10:49
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertGoodsDTO implements Serializable {

    Long goodsId;
    Double seckillPrice;
    Integer stockCount;
    Date startDate;
    Date endDate;
}
