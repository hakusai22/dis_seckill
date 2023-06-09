package com.seckill.dis.common.api.mq.vo;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 在MQ中传递的秒杀信息
 * 包含参与秒杀的用户和商品的id
 *
 * @author hakusai
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class SkMessage implements Serializable {

    private long userID;
    private long goodsId;
}
