package com.seckill.dis.common.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * seckill_order 表
 *
 * @author hakusai
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SeckillOrder implements Serializable {

  private Long id;

  private Long userId;

  private Long orderId;

  private Long goodsId;

  private Date createTime;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


}
