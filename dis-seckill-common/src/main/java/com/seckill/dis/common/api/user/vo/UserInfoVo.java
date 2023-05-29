package com.seckill.dis.common.api.user.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息
 * <p>
 * 注：因为需要通过网络传输此model，所以需要序列化
 *
 * @author hakusai
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVo implements Serializable {

    private Integer uuid;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private int sex;
    private String birthday;
    private String lifeState;
    private String biography;
    private String address;
    private String headAddress;
    private long beginTime; // 创建时间
    private long updateTime;// 更新时间

}