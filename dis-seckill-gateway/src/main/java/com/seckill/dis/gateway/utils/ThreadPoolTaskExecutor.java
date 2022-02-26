package com.seckill.dis.gateway.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolTaskExecutor {


    @Bean(name = "Executor")
    public org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor(){
        org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor executor = new org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor();
        //配置暂时写死 后续可引入配置文件
        //核心线程数
        executor.setCorePoolSize(5);
        //最大线程数
        executor.setMaxPoolSize(10);
        //队列最大长度
        executor.setQueueCapacity(1000);
        //线程池维护线程所允许的空闲时间
        executor.setKeepAliveSeconds(20);
        //线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //线程初始化
        executor.initialize();
        return executor;
    }
}
