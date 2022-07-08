package com.chan.mq.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CHAN
 * @since 2022/7/8
 */
@Configuration
public class DeadLetterConfig {
    // 声明普通的queue队列，并且为其指定死信交换机：dl.direct
    // 这样这个普通的队列中如果有死信，那么死信就会进到这个死信交换机
    @Bean
    public Queue normalQueue() {
        return QueueBuilder.durable("normal.queue")// 指定队列名称，并持久化
                .deadLetterExchange("dl.direct")// 指定死信交换机
                .deadLetterRoutingKey("dl")// 指定死信交换机和死信队列绑定的key
                .build();
    }
    // 声明死信交换机 dl.direct
    @Bean
    public DirectExchange dlExchange(){
        return new DirectExchange("dl.direct", true, false);
    }
    // 声明存储死信的队列 dl.queue
    @Bean
    public Queue dlQueue(){
        return new Queue("dl.queue", true);
    }
    // 将死信队列 与 死信交换机绑定
    @Bean
    public Binding dlBinding(){
        return BindingBuilder.bind(dlQueue()).to(dlExchange()).with("dl");
    }
}
