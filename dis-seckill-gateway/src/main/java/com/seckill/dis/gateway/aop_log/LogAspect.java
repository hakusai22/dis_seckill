package com.seckill.dis.gateway.aop_log;

import com.alibaba.fastjson.JSON;
import com.seckill.dis.common.util.HttpContextUtils;
import com.seckill.dis.common.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author xizizzz
 * @description: TODO
 * @date 2021-7-30下午 08:07
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    //切点 自定义注解
    @Pointcut("@annotation(com.seckill.dis.gateway.aop_log.LogAnnotation)")
    public void logPointCut() {
    }

    //环绕通知
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        recordLog(point, time);
        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        System.out.println();
        log.info("=====================log start================================");
        log.info("module:{}", logAnnotation.module());
        log.info("operation:{}", logAnnotation.operation());

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}", className + "." + methodName + "()");
//      //请求的参数
        Object[] args = joinPoint.getArgs();

        if(args!=null&&args.length>0){
//            String params = JSON.toJSONString(args[0]);
//            log.info("params:{}", params);
        }
        //获取request 设置IP地址
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.info("ip:{}", IpUtils.getIpAddr(request));


        log.info("excute time : {} ms", time);
        log.info("=====================log end================================");
        System.out.println();
    }
}
