package com.seckill.dis.gateway.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @description: 自定义线程池-公用
 */
@Configuration
@EnableAsync
public class AsyncThreadPoolExecutor {

  @Bean(name = "AsyncExecutor")
  public Executor initProjectThreadPollExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    //配置暂时写死 后续可引入配置文件
    executor.setThreadNamePrefix("async-task-");
    //核心线程数
    executor.setCorePoolSize(10);
    //最大线程数
    executor.setMaxPoolSize(50);
    //队列最大长度
    executor.setQueueCapacity(1000);
    //线程池维护线程所允许的空闲时间
    executor.setKeepAliveSeconds(10);
    //线程池对拒绝任务(无线程可用)的处理策略
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    //线程初始化
    executor.initialize();
    return executor;
  }
}
