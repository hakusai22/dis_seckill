package com.seckill.dis.common.api.user.vo;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册参数
 *
 * @author hakusai
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterVo implements Serializable {

    @NotNull
    private Long phone;

    @NotNull
    private String nickname;


    private String head;

    @NotNull
    private String password;

    @NotNull
    private String email;

    @NotNull
    private String address;

}
