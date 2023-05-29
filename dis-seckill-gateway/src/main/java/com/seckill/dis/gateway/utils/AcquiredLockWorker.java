package com.seckill.dis.gateway.utils;

/**
 * 获取锁后需要处理的逻辑
 *
 * @author yinpeng
 */
@FunctionalInterface
public interface AcquiredLockWorker<T> {
  /**
   * 获取锁后处理的逻辑
   *
   * @return
   * @throws Exception
   */
  T invokeAfterLockAcquire() throws Exception;
}
