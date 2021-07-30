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
     * 首页
     * @return
     */
    @LogAnnotation(module = "首页",operation = "login登录页面")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "login";// login页面
    }
}
