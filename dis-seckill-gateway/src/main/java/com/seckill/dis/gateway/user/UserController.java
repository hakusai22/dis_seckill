package com.seckill.dis.gateway.user;

import com.seckill.dis.common.api.cache.vo.SkUserKeyPrefix;
import com.seckill.dis.common.api.user.UserServiceApi;
import com.seckill.dis.common.api.user.vo.LoginVo;
import com.seckill.dis.common.api.user.vo.RegisterVo;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.common.result.CodeMsg;
import com.seckill.dis.common.result.Result;
import com.seckill.dis.common.util.MD5Util;
import com.seckill.dis.gateway.aop_log.LogAnnotation;
import com.seckill.dis.gateway.exception.GlobalException;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 用户接口
 * @author xizizzz
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Reference(interfaceClass = UserServiceApi.class)
    UserServiceApi userService;

    /**
     * 用户登录接口
     * @param response 响应
     * @param loginVo  用户登录请求的表单数据（将表单数据封装为了一个Vo：Value Object）
     *                 注解@Valid用于校验表单参数，校验成功才会继续执行业务逻辑，否则，
     *                 请求参数校验不成功抛出异常
     */
    @LogAnnotation(module = "用户登录接口",operation = "用户登录接口")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> login(HttpServletResponse response, @Valid LoginVo loginVo) {
        String token = userService.login(loginVo);
        logger.info("token: " + token);
        // 将token写入cookie中, 然后传给客户端（一个cookie对应一个用户，这里将这个cookie的用户信息写入redis中）
        Cookie cookie = new Cookie(UserServiceApi.COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(SkUserKeyPrefix.TOKEN.expireSeconds());// 保持与redis中的session一致
        cookie.setPath("/");
        response.addCookie(cookie);
        // 返回登陆成功
        return Result.success(token);
    }

    /**
     * 注册跳转
     */
    @LogAnnotation(module = "注册跳转",operation = "注册跳转")
    @RequestMapping(value = "doRegister", method = RequestMethod.GET)
    public String doRegister() {
        logger.info("doRegister()");
        return "register";
    }

    /**
     * 注册接口
     */
    @LogAnnotation(module = "注册接口",operation = "注册接口")
    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> register(RegisterVo registerVo) {
        logger.info("RegisterVo = " + registerVo);
        if (registerVo == null) {
            throw new GlobalException(CodeMsg.FILL_REGISTER_INFO);
        }
        CodeMsg codeMsg = userService.register(registerVo);
        return Result.info(codeMsg);
    }


}
