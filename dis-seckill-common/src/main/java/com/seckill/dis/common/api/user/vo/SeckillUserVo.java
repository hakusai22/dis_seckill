package com.seckill.dis.common.api.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀用户信息
 * @author xizizzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillUserVo implements Serializable{

    private Long uuid;
    private Long phone;
    private String nickname;
    private String password;
    private String salt;
    private String head;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date registerDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date lastLoginDate;
    private Integer loginCount;
}

