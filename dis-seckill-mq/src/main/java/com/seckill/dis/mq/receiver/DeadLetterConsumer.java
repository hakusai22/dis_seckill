package com.seckill.dis.mq.receiver;

import com.rabbitmq.client.Channel;
import com.seckill.dis.common.api.order.OrderServiceApi;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author hakusai
 * @description: TODO
 * @date 2021-8-14下午 10:06
 */
public class DeadLetterConsumer {

  private static final Logger logger = LoggerFactory.getLogger(DeadLetterConsumer.class);

  @Reference(interfaceClass = OrderServiceApi.class)
  OrderServiceApi orderService;

  @RabbitListener(queues = "dead_letters_queue_name_order")
  public void handler(Message message, Channel channel) throws IOException {
    /**
     * 发送消息之前，根据订单ID去查询订单的状态，如果已支付不处理，如果未支付，则更新订单状态为取消状态。
     */
    // 从队列中取出订单号
    byte[] body = message.getBody();
    String orderId = new String(body, StandardCharsets.UTF_8);
    logger.info("消费者接收到订单：" + orderId);
    String orderStatus = orderService.findByOrderStatus(orderId);

    logger.info("订单状态： " + orderStatus);
    if (!"1".equals(orderStatus)) {
      //取消订单
      orderService.updateOrderStatus(orderId);
    }
    if (channel.isOpen()) {
      channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
  }

}
