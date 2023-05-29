package com.seckill.dis.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀用户信息
 * @author hakusai
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillUser implements Serializable{

    private Long uuid;

    private Long phone;

    private String nickname;

    private String password;

    private String salt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date registerDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date lastLoginDate;

    private Integer loginCount;

    private String registerDateString;

    private String lastLoginDateString;

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getId() {
        return uuid;
    }

    public void setId(Long uuid) {
        this.uuid = uuid;
    }
}

