package com.seckill.dis.common.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * seckill_goods
 *
 * @author hakusai
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillGoods implements Serializable {

  private Long id;

  private Long goodsId;

  private Double seckillPrice;

  private Integer stockCount;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date startDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date endDate;

  @JsonSerialize(using = ToStringSerializer.class)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

}
