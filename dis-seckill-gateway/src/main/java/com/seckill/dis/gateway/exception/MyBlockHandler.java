package com.seckill.dis.gateway.exception;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.common.result.CodeMsg;
import com.seckill.dis.common.result.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (c) 2023
 * All rights reserved
 * Author: hakusai22@qq.com
 */

@Component
public class MyBlockHandler {
  private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  public static Result<Integer> blockHandlerFordoSeckill(UserVo user,
      @RequestParam("goodsId") long goodsId, BlockException ex) {
    logger.info("商品ID:" + goodsId + " 秒杀接口限流");
    return Result.error(CodeMsg.SERVER_BUSY);
  }

  public static Result<String> blockHandlerForVerifyCodel(
      HttpServletResponse response, UserVo user,
      @RequestParam("goodsId") long goodsId, BlockException ex) {
    logger.info("商品ID:" + goodsId + " 验证码接口限流");
    return Result.error(CodeMsg.SERVER_BUSY);
  }
}
    
    

