package com.seckill.dis.gateway.aop_log;

import java.lang.annotation.*;

/**
 * @author xizizzz
 * @description: 日志注解
 * @date 2021-7-30下午 08:07
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    String module() default "";

    String operation() default "";
}