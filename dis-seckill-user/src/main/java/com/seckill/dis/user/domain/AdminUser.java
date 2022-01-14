package com.seckill.dis.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author yinpeng8
 * @Date 2022/1/9 16:51
 * @Description:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUser  implements Serializable {
    private Long uuid;
    private Long phone;
    private String nickname;
    private String password;
    private String salt;

}

