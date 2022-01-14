package com.seckill.dis.common.api.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息
 * <p>
 * 注：因为需要通过网络传输此model，所以需要序列化
 * @author xizizzz
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserVo implements Serializable {

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
