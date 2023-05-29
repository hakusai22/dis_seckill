package com.seckill.dis.gateway.alipay;

import com.alipay.api.AlipayApiException;
import com.seckill.dis.common.api.order.OrderServiceApi;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Copyright (c) 2023
 * All rights reserved
 * Author: hakusai22@qq.com
 */

@Service
public class PayServiceImpl implements PayService {

  @Autowired
  private Alipay alipay;

  @Reference(interfaceClass = OrderServiceApi.class)
  OrderServiceApi orderService;

  @Override
  public String aliPay(AlipayBean alipayBean) throws AlipayApiException {
    orderService.updateOrderById(Long.parseLong(alipayBean.getOut_trade_no()));
    return alipay.pay(alipayBean);
  }

}