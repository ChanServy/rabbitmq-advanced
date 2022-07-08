package com.chan.mq.consumer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CHAN
 * @since 2022/7/8
 */
@Configuration
public class QueueConfig {

    @Bean
    public Queue simpleQueue(){
        return new Queue("simple.queue");
    }
    @Bean
    public Queue workQueue(){
        return new Queue("work.queue");
    }
}
