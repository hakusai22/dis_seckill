package com.seckill.dis.common.api.goods.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author yinpeng8
 * @Date 2022/1/14 10:49
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertGoodsDTO {

    Long goodsId;
    Double seckillPrice;
    Integer stockCount;
    Date startDate;
    Date endDate;

}
