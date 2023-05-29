package com.seckill.dis.gateway.alipay;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.seckill.dis.common.api.order.OrderServiceApi;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

/**
 * 支付宝支付接口
 *
 * @author Louis
 * @date Dec 12, 2018
 */
@Component
public class Alipay {

  @Reference(interfaceClass = OrderServiceApi.class)
  OrderServiceApi orderService;

  /**
   * 支付接口
   *
   * @param alipayBean
   * @return
   * @throws AlipayApiException
   */
  public String pay(AlipayBean alipayBean) throws AlipayApiException {
    // 1、获得初始化的AlipayClient
    String serverUrl = AlipayProperties.getGatewayUrl();
    String appId = AlipayProperties.getAppId();
    String privateKey = AlipayProperties.getPrivateKey();
    String format = "json";
    String charset = AlipayProperties.getCharset();
    String alipayPublicKey = AlipayProperties.getPublicKey();
    String signType = AlipayProperties.getSignType();
    String returnUrl = AlipayProperties.getReturnUrl();
    String notifyUrl = AlipayProperties.getNotifyUrl();
    AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
    // 2、设置请求参数
    AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
    // 页面跳转同步通知页面路径
    alipayRequest.setReturnUrl(returnUrl);
    // 服务器异步通知页面路径
    alipayRequest.setNotifyUrl(notifyUrl);
//        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
//        model.setOutTradeNo("20150320010112312323");
//        model.setProductCode("FAST_INSTANT_TRADE_PAY");
//        model.setSubject("Iphone6 16G");
//        model.setTotalAmount("0.01");
    //以上四个为必选内容。ProductCode是写死的，订单号这些可以自己生成
    //model.setTimeoutExpress("30m");
//        alipayRequest.setReturnUrl("https://www.hakusai.top/");//据说是支付成功后返回的页面
//        alipayRequest.setNotifyUrl("https://www.hakusai.top/");//回调的页面，可以用来执行支付成功后的接口调用什么的
//        alipayRequest.setBizModel(model);
    // 封装参数
    alipayRequest.setBizContent(JSON.toJSONString(alipayBean));
    // 3、请求支付宝进行付款，并获取支付结果
    String result = alipayClient.pageExecute(alipayRequest).getBody();
    System.out.println("getOut_trade_no" + alipayBean.getOut_trade_no());
    orderService.updateOrderById(Long.parseLong(alipayBean.getOut_trade_no()));
    // 返回付款信息
    return result;
  }
}