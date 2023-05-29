package com.seckill.dis.common.util;

import java.util.UUID;


/**
 * UUID工具类用于生成session
 *
 * @author hakusai
 */
public class UUIDUtil {
  public static String uuid() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}
