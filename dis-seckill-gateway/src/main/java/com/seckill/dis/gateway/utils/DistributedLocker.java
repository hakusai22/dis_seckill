package com.seckill.dis.gateway.utils;

/**
 * 基于redis的分布式锁
 * 目前基于set NX PX 来实现，存在极端问题，后续集成redlock
 *
 * @author yinpeng
 * @version 1.0.0
 */
public interface DistributedLocker {

  long TIMEOUT_MILLIS = 30000;

  int RETRY_TIMES = Integer.MAX_VALUE;

  long SLEEP_MILLIS = 500;

  /**
   * 设置锁并指定获取锁后的处理类
   *
   * @param key    锁的名称
   * @param worker 获取锁后的处理类
   * @param <T>
   * @return
   * @throws Exception
   */
  <T> T lock(String key, AcquiredLockWorker<T> worker) throws Exception;

  /**
   * 设置锁、上锁失败重试次数并指定获取锁后的处理类
   *
   * @param key        锁的名称
   * @param retryTimes 重试次数
   * @param worker     获取锁后的处理类
   * @param <T>
   * @return
   * @throws Exception
   */
  <T> T lock(String key, int retryTimes,
      AcquiredLockWorker<T> worker) throws Exception;

  /**
   * 设置锁、上锁失败重试次数、休眠时间并指定获取锁后的处理类
   *
   * @param key         锁的名称
   * @param retryTimes  重试次数
   * @param sleepMillis 休眠时间
   * @param worker      获取锁后的处理类
   * @param <T>
   * @return
   * @throws Exception
   */
  <T> T lock(String key, int retryTimes, long sleepMillis,
      AcquiredLockWorker<T> worker) throws Exception;

  /**
   * 设置锁、过期时间并指定获取锁后的处理类
   *
   * @param key    锁的名称
   * @param expire 过期时间
   * @param worker 获取锁后的处理类
   * @param <T>
   * @return
   * @throws Exception
   */
  <T> T lock(String key, long expire,
      AcquiredLockWorker<T> worker) throws Exception;

  /**
   * 设置锁、过期时间、获取失败重试次数并指定获取锁后的处理类
   *
   * @param key        锁的名称
   * @param expire     过期时间
   * @param retryTimes 重试次数
   * @param worker     获取锁后的处理类
   * @param <T>
   * @return
   * @throws Exception
   */
  <T> T lock(String key, long expire, int retryTimes,
      AcquiredLockWorker<T> worker) throws Exception;

  /**
   * 设置锁、过期时间、获取失败重试次数、休眠时间并指定获取锁后的处理类
   *
   * @param key         锁的名称
   * @param expire      过期时间
   * @param retryTimes  重试次数
   * @param sleepMillis 休眠时间
   * @param worker      获取锁后的处理类
   * @param <T>
   * @return
   * @throws Exception
   */
  <T> T lock(String key, long expire, int retryTimes, long sleepMillis,
      AcquiredLockWorker<T> worker) throws Exception;
}
