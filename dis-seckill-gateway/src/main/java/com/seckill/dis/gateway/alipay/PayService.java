package com.seckill.dis.gateway.alipay;

import com.alipay.api.AlipayApiException;

/**
 * 支付服务
 *
 * @author Louis
 * @date Dec 12, 2018
 */
public interface PayService {

  /**
   * 支付宝支付接口
   *
   * @param alipayBean
   * @return
   * @throws AlipayApiException
   */
  String aliPay(AlipayBean alipayBean) throws AlipayApiException;

}