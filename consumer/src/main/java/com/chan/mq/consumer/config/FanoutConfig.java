package com.chan.mq.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CHAN
 * @since 2022/7/8
 */
@Configuration
public class FanoutConfig {

    /**
     * 声明交换机，默认情况下，由SpringAMQP声明的交换机都是持久化的。
     * @return Fanout类型交换机
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        // 三个参数：交换机名称、是否持久化、当没有queue与其绑定时是否自动删除
        return new FanoutExchange("fanout.exchange", true, false);
    }

    /**
     * 默认情况下，由SpringAMQP声明的队列都是持久化的。
     * 第1个队列
     */
    @Bean
    public Queue fanoutQueue1(){
        return new Queue("fanout.queue1");
        // 使用QueueBuilder构建队列，durable就是持久化的
        // return QueueBuilder.durable("fanout.queue1").build();
    }


    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding bindingQueue1(){
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }

    /**
     * 第2个队列
     */
    @Bean
    public Queue fanoutQueue2(){
        return new Queue("fanout.queue2");
    }

    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding bindingQueue2(){
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }

}
