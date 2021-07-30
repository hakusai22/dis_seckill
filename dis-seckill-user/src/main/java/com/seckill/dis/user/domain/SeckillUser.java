package com.seckill.dis.user.domain;

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
public class SeckillUser implements Serializable{

    private Long uuid;
    private Long phone;
    private String nickname;
    private String password;
    private String salt;
    private String head;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;
}

