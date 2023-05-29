package com.seckill.dis.gateway.index;

import com.seckill.dis.gateway.aop_log.LogAnnotation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 根路径，默认页面
 */
@Controller
public class Index {
  /**
   * 用户首页
   *
   * @return
   */
  @LogAnnotation(module = "首页", operation = "login登录页面")
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String userIndex() {
    return "login";// login页面
  }

  /**
   * 管理员首页
   *
   * @return
   */
  @LogAnnotation(module = "首页", operation = "login登录页面")
  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  public String adminIndex() {
    return "adminLogin";// login页面
  }

  /**
   * 添加商品首页
   *
   * @return
   */
  @LogAnnotation(module = "添加商品首页", operation = "添加商品首页")
  @RequestMapping(value = "/insertGoods", method = RequestMethod.GET)
  public String insertGoods() {
    return "insertGoods";// login页面
  }
}
