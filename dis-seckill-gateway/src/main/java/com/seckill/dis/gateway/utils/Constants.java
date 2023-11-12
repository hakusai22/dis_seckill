package com.seckill.dis.gateway.utils;

import java.io.File;

/**
 * Copyright (c) 2023
 * All rights reserved
 * Author: hakusai22@qq.com
 */

public class Constants {

  private Constants() {
  }

  /**
   * 中断提示
   */
  public static final String Q_OR_CTRL_C_ABORT_MSG = "Press Q or Ctrl+C to abort.";

  /**
   * 空字符串
   */
  public static final String EMPTY_STRING = "";

  /**
   * 命令提示符
   */
  public static final String DEFAULT_PROMPT = "$ ";

  /**
   * 方法执行耗时
   */
  public static final String COST_VARIABLE = "cost";

  public static final String CMD_HISTORY_FILE = System.getProperty("user.home") + File.separator + ".arthas" + File.separator + "history";
}
