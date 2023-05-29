package com.seckill.dis.common.api.user;

import com.seckill.dis.common.api.user.vo.LoginVo;
import com.seckill.dis.common.api.user.vo.RegisterVo;
import com.seckill.dis.common.api.user.vo.UserInfoVo;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.common.domain.SeckillUser;
import com.seckill.dis.common.result.CodeMsg;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

/**
 * 用户服务接口
 * @author hakusai
 */
public interface UserServiceApi {

    String COOKIE_NAME_TOKEN = "token";


    /**
     * 更新登录的次数和登录的时间
     */
    void updateLoginCount(Long phone) throws ParseException;

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
     * 获取用户信息
     */
    List<SeckillUser> getAllUserInfo();


    /**
     * 更新用户信息
     * @param userInfoVo
     */
    UserInfoVo updateUserInfo(UserInfoVo userInfoVo);

    /**
     * 用户登录
     * @param loginVo
     */
    String login(@Valid LoginVo loginVo);

    /**
     * 管理员登录
     * @param loginVo
     */
    String adminLogin(@Valid LoginVo loginVo);

    /**
     * 根据phone获取用户
     * @param phone
     */
    UserVo getUserByPhone(long phone);

    void deleteUser(long uuid);
}
