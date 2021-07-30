package com.seckill.dis.gateway.config.access;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * 用户访问拦截的注解
 * 主要用于防止刷功能的实现
 * @author xizizzz
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {

    /**
     * 两次请求的最大有效时间间隔，即视两次请求为同一状态的时间间隔
     */
    int seconds();

    /**
     * 最大请求次数
     */
    int maxAccessCount();

    /**
     * 是否需要重新登录
     */
    boolean needLogin() default true;
}
