package com.seckill.dis.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过配置文件获取消息队列
 * @author xizizzz
 */
@Configuration
public class MQConfig {

    // 秒杀 routing key, 生产者沿着 routingKey 将消息投递到 exchange 中
    public static final String SK_ROUTING_KEY = "routing.sk";

    // 秒杀交换机
    public static final String SECKILL_EXCHANGE = "seckill.exchange";

    // 秒杀队列名称A
    public static final String SECKILL_QUEUE_A = "seckill.queueA";

    //死信交换机名称
    public static final String DEAD_LETTER_EXCHANGE = "dead_exchange";
    //死信队列名称
    public static final String DEAD_LETTER_QUEUE = "dead.queue";


    // 声明秒杀交换机 xExchange
    @Bean("xExchange")
    public DirectExchange xExchange() {
        return new DirectExchange(SECKILL_EXCHANGE);
    }

    // 声明死信交换机 yExchange
    @Bean("yExchange")
    public DirectExchange yExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    //声明秒杀队列A TTL为10S
    @Bean("queueA")
    public Queue queueA() {
        Map<String, Object> args = new HashMap<>(3);
        //声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        //声明当前队列的死信路由 key
        args.put("x-dead-letter-routing-key", "YD");
        // 设置队列的最大长度值为0
        args.put("x-max-length", 10);
        //声明队列的 TTL
//        args.put("x-message-ttl", 10000);
        return QueueBuilder.durable(SECKILL_QUEUE_A).withArguments(args).build();
    }

    // 声明秒杀队列A绑定 X交换机
    @Bean
    public Binding queueaBindingX(@Qualifier("queueA") Queue queueA,
                                  @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    //声明死信队列 QD
    @Bean("queueD")
    public Queue queueD() {
        return new Queue(DEAD_LETTER_QUEUE);
    }

    //声明死信交换机和队列 QD 绑定关系
    @Bean
    public Binding deadLetterBindingQAD(@Qualifier("queueD") Queue queueD,
                                        @Qualifier("yExchange") DirectExchange yExchange) {
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }

    /**
     * Direct模式 交换机exchange
     * 生成用于秒杀的queue
     */
//    @Bean
//    public Queue seckillQueue() {
//        return new Queue(SECKILL_QUEUE, true);
//    }

    // 实例化 RabbitTemplate
    @Bean
    @Scope("prototype")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        return template;
    }
}