package com.seckill.dis.cache.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * jedis连接池
 */
@Component
public class RedisPoolFactory {
    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool JedisPoolFactory() {
        // jedis连接池设置application.prperties配置的属性
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);

        // 记得配置密码
        JedisPool pool = new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(), redisConfig.getTimeout(), redisConfig.getPassword());
        return pool;
    }
}
