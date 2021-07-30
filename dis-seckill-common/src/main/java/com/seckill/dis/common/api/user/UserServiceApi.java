package com.seckill.dis.common.api.user;

import com.seckill.dis.common.api.user.vo.LoginVo;
import com.seckill.dis.common.api.user.vo.RegisterVo;
import com.seckill.dis.common.api.user.vo.UserInfoVo;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.common.result.CodeMsg;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 用户服务接口
 * @author xizizzz
 */
public interface UserServiceApi {

    String COOKIE_NAME_TOKEN = "token";

    /**
     * 注册
     * @param userModel
     */
    CodeMsg register(RegisterVo userModel);

    /**
     * 检查用户名是否存在
     * @param username
     */
    boolean checkUsername(String username);

    /**
     * 获取用户信息
     * @param uuid
     */
    UserInfoVo getUserInfo(int uuid);

    /**
     * 更新用户信息
     * @param userInfoVo
     */
    UserInfoVo updateUserInfo(UserInfoVo userInfoVo);

    /**
     * 登录
     * @param loginVo
     */
    String login(@Valid LoginVo loginVo);

    /**
     * 根据phone获取用户
     * @param phone
     */
    UserVo getUserByPhone(long phone);
}
