package com.seckill.dis.gateway.utils;

import lombok.AllArgsConstructor;

import java.util.Set;

/**
 * @description: 异步模糊删除缓存任务
 */
@AllArgsConstructor
public class AsyncMatchDelCacheThread implements  Runnable {

    private final RedisUtil redisUtil;
    /**
     * 需要模糊删除的key集合
     */
    private final Set<String> matchKeySet;

    @Override
    public void run() {
        for (String matchKey : matchKeySet) {
            // 通过scan扫描key并删除缓存
            redisUtil.matchDelByScan(matchKey);
        }
    }
}
