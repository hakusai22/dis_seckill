package com.seckill.dis.mq.service;

import com.seckill.dis.common.api.mq.MqProviderApi;
import com.seckill.dis.common.api.mq.vo.SkMessage;
import com.seckill.dis.mq.config.MQConfig;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.UUID;


/**
 * 消息队列服务化（消息生产者）
 * @author xizizzz
 */
@Service(interfaceClass = MqProviderApi.class)
public class MqProviderImpl implements MqProviderApi, RabbitTemplate.ConfirmCallback {

    private static Logger logger = LoggerFactory.getLogger(MqProviderImpl.class);

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public MqProviderImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        // 设置 ack 回调
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 将用户秒杀信息投递到MQ中（使用direct模式的exchange）
     * @param message
     */
    @Override
    public void sendSkMessage(SkMessage message) {
        logger.info("MQ send message: " + message);
        // 秒杀消息关联的数据 UUID
        CorrelationData skCorrData = new CorrelationData(UUID.randomUUID().toString());
        // 第一个参数为消息队列名(此处也为routingKey)，第二个参数为发送的消息
        rabbitTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, message, skCorrData);
    }

    /**
     * MQ ack 机制
     * TODO 完善验证机制，确保消息能够被消费，且不影响消息吞吐量
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        logger.info("SkMessage UUID: " + correlationData.getId());
        if (ack) {
            logger.info("SkMessage 消息消费成功！");
        } else {
            System.out.println("SkMessage 消息消费失败！");
            logger.info("CallBackConfirm Cause: " + cause);
        }

    }

    @Bean
    /***
     * @Description: 绑定correlationId、以及消息持久化
     * @return: org.springframework.amqp.core.MessagePostProcessor
     **/
    public MessagePostProcessor correlationIdProcessor() {
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message, Correlation correlation) {
                MessageProperties messageProperties = message.getMessageProperties();
                if (correlation instanceof CorrelationData) {
                    String correlationId = ((CorrelationData) correlation).getId();
                    messageProperties.setCorrelationId(correlationId);
                }
                // 持久化处理
                messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            }
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties messageProperties = message.getMessageProperties();
                messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            }
        };
        return messagePostProcessor;
    }
}