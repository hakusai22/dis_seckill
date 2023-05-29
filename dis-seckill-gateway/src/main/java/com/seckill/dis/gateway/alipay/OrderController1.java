package com.seckill.dis.gateway.alipay;

import com.alipay.api.AlipayApiException;
import com.seckill.dis.common.api.order.OrderServiceApi;
import com.seckill.dis.common.util.UUIDUtil;
import com.seckill.dis.gateway.utils.SnowflakeIdGenerator;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 订单接口
 *
 * @author Louis
 * @date Dec 12, 2018
 */
@RestController()
@RequestMapping("/order1")
public class OrderController1 {

  @Autowired
  private PayService payService;

  @Reference(interfaceClass = OrderServiceApi.class)
  OrderServiceApi orderService;

  /**
   * 阿里支付
   *
   * @param subject
   * @param body
   * @return
   * @throws AlipayApiException
   */
  @PostMapping(value = "/alipay")
  public String alipay(String outTradeNo, String subject, String totalAmount,
      String body) throws AlipayApiException {
    AlipayBean alipayBean = new AlipayBean();
    alipayBean.setOut_trade_no(outTradeNo);
    alipayBean.setSubject(subject);
    alipayBean.setTotal_amount(totalAmount);
    alipayBean.setBody(body);
    orderService.updateOrderById(Long.parseLong(alipayBean.getOut_trade_no()));
    return payService.aliPay(alipayBean);
  }
}