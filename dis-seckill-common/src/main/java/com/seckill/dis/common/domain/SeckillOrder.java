package com.seckill.dis.common.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * seckill_order 表
 * @author xizizzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SeckillOrder implements Serializable{

    private Long id;

    private Long userId;

    private Long orderId;

    private Long goodsId;
}
