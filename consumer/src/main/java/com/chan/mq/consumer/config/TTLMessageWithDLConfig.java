package com.chan.mq.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TTL延时消息也是基于死信交换机和死信队列实现
 *
 * @author CHAN
 * @since 2022/7/8
 */
@Configuration
public class TTLMessageWithDLConfig {
    // 声明一个队列，并且指定TTL，这个队列设定了死信交换机为dl.ttl.direct
    @Bean
    public Queue ttlQueue() {
        return QueueBuilder.durable("ttl.queue")// 指定队列名称，并持久化
                .ttl(10000)// 设置队列的超时时间，10秒，普通队列超时，其中的消息就会变为死信进入死信交换机，我们只需要监听死信队列就可以实现延迟消息的效果
                .deadLetterExchange("dl.ttl.direct")// 指定死信交换机
                .deadLetterRoutingKey("dl.ttl")// 指定死信交换机和死信队列绑定的key
                .build();
    }
    // 声明交换机，将ttl.queue与交换机绑定
    @Bean
    public DirectExchange ttlExchange() {
        return new DirectExchange("ttl.direct");
    }
    @Bean
    public Binding ttlBinding() {
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange()).with("ttl");
    }
    // 声明死信交换机
    @Bean
    public DirectExchange dlTTLExchange() {
        return new DirectExchange("dl.ttl.direct");
    }
    // 声明死信队列
    @Bean
    public Queue dlTTLQueue() {
        return new Queue("dl.ttl.queue", true);
    }
    // 绑定死信队列到死信交换机
    @Bean
    public Binding dlTTLBinding() {
        return BindingBuilder.bind(dlTTLQueue()).to(dlTTLExchange()).with("dl.ttl");
    }
}
