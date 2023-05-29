package com.seckill.dis.gateway.user;

import com.seckill.dis.common.api.cache.vo.GoodsKeyPrefix;
import com.seckill.dis.common.api.cache.vo.SkUserKeyPrefix;
import com.seckill.dis.common.api.goods.vo.GoodsVo;
import com.seckill.dis.common.api.seckill.SeckillServiceApi;
import com.seckill.dis.common.api.user.UserServiceApi;
import com.seckill.dis.common.api.user.vo.LoginVo;
import com.seckill.dis.common.api.user.vo.RegisterVo;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.common.domain.SeckillUser;
import com.seckill.dis.common.result.CodeMsg;
import com.seckill.dis.common.result.Result;
import com.seckill.dis.common.util.MD5Util;
import com.seckill.dis.gateway.aop_log.LogAnnotation;
import com.seckill.dis.gateway.exception.GlobalException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

/**
 * 用户接口
 *
 * @author hakusai
 */
@Controller
@RequestMapping("/user/")
public class UserController {
  private static Logger logger = LoggerFactory.getLogger(UserController.class);

  @Reference(interfaceClass = UserServiceApi.class)
  UserServiceApi userService;

  /**
   * 因为在redis缓存中不存页面缓存时需要手动渲染，所以注入一个视图解析器，自定义渲染
   */
  @Autowired
  ThymeleafViewResolver thymeleafViewResolver;


  /**
   * 用户登录接口
   *
   * @param response 响应
   * @param loginVo  用户登录请求的表单数据（将表单数据封装为了一个Vo：Value Object）
   *                 注解@Valid用于校验表单参数，校验成功才会继续执行业务逻辑，否则，
   *                 请求参数校验不成功抛出异常
   */
  @LogAnnotation(module = "用户登录接口", operation = "用户登录接口")
  @RequestMapping(value = "login", method = RequestMethod.POST)
  @ResponseBody
  public Result<String> login(HttpServletResponse response,
      @Valid LoginVo loginVo) throws ParseException {
    String token = userService.login(loginVo);
    userService.updateLoginCount(Long.valueOf(loginVo.getMobile()));
    logger.info("token: " + token);
    // 将token写入cookie中, 然后传给客户端（一个cookie对应一个用户，这里将这个cookie的用户信息写入redis中）
    Cookie cookie = new Cookie(UserServiceApi.COOKIE_NAME_TOKEN, token);
    cookie.setMaxAge(SkUserKeyPrefix.TOKEN.expireSeconds());// 保持与redis中的session一致
    cookie.setPath("/");
    response.addCookie(cookie);
    // 返回登陆成功
    return Result.success(token);
  }

  @LogAnnotation(module = "管理员登录接口", operation = "管理员登录接口")
  @RequestMapping(value = "adminLogin", method = RequestMethod.POST)
  @ResponseBody
  public Result<String> adminLogin(HttpServletResponse response,
      @Valid LoginVo loginVo) {
    String token = userService.adminLogin(loginVo);
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
  @LogAnnotation(module = "注册跳转", operation = "注册跳转")
  @RequestMapping(value = "doRegister", method = RequestMethod.GET)
  public String doRegister() {
    logger.info("doRegister()");
    return "register";
  }

  /**
   * 注册接口
   */
  @LogAnnotation(module = "注册接口", operation = "注册接口")
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

  /**
   * 注册接口
   */
  @LogAnnotation(module = "查询全部的用户", operation = "查询全部的用户")
  @RequestMapping(value = "getAllUser", method = RequestMethod.GET)
  @ResponseBody
  public String getAllUser(HttpServletRequest request,
      HttpServletResponse response, Model model) {
    List<SeckillUser> usersList = userService.getAllUserInfo();
    model.addAttribute("usersList", usersList);
    // 3. 渲染html
    WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
    // (第一个参数为渲染的html文件名，第二个为web上下文：里面封装了web应用的上下文)
    String html = thymeleafViewResolver.getTemplateEngine().process("userInfo_list", webContext);
    return html;
  }


  @GetMapping(value = "deleteUser")
  @ResponseBody
  public String deleteUser(@RequestParam("uuid") long uuid, Model model,
      HttpServletResponse response, HttpServletRequest request,
      UserVo user) throws ParseException {
    userService.deleteUser(uuid);

    List<SeckillUser> usersList = userService.getAllUserInfo();
    model.addAttribute("usersList", usersList);
    // 3. 渲染html
    WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
    // (第一个参数为渲染的html文件名，第二个为web上下文：里面封装了web应用的上下文)
    String html = thymeleafViewResolver.getTemplateEngine().process("userInfo_list", webContext);
    return html;
  }


}
