package com.seckill.dis.common.api.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author yinpeng8
 * @Date 2022/1/14 16:16
 * @Description:
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class SeckillResultDTO {

    private long orderId;

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

}
