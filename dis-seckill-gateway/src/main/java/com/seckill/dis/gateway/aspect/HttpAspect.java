package com.seckill.dis.gateway.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author hakusai
 * @description: 面向切面 打印日志 监控contriller层接口
 * @date 2021-6-23下午 07:48
 */
@Aspect //切面注解
@Order(1) // 控制多个Aspect的执行顺序，越小越先执行
@Component
@Slf4j  //日志
public class HttpAspect {

  // 切点  controller层的所有方法
  @Pointcut("execution(public * com.seckill.dis.gateway.*.*(..))")
  public void log() {
  }

  // 前置通知,ntroller层的所有方法调用前进行前置处理
  @Before(value = "log()")
  public void doBefore(JoinPoint joinPoint) {
    //切入点获取通知的签名
    Signature signature = joinPoint.getSignature();
    //切入点获取参数
    Object[] args = joinPoint.getArgs();
    //切入点的方法名称
    String methodName = signature.getName();
    log.info("【发起请求】,method={},args={}", methodName, args);
  }

  //后置返回 controller层的所有方法
  @AfterReturning(value = "log()", returning = "object")
  public void doAfterReturning(JoinPoint joinPoint, Object object) {
    // object为方法return的数据
    log.info("【返回数据】,response={}", object);
  }
}