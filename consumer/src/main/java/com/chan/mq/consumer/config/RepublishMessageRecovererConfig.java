package com.chan.mq.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消费者自动ACK的情况下，当消费者有异常，Spring会感知到RabbitListener中的异常，不会ACK并且会将消息requeue回原来的队列中。
 * 但是requeue回队列中就又会立刻被消费者收到并且异常，这样就会一直requeue，从而降低MQ的吞吐量。因此我们配置了Spring本地重试，
 * 这样本地重试一定次数（我们可以配置）后，SpringAMQP会抛出AmqpRejectAndDontRequeueException（说明本地重试触发了消息不会requeue了）
 * 并且SpringAMQP会返回ACK给RabbitMQ服务器将消息删除。
 *
 * 结论：
 * 开启本地重试时，消息处理过程中抛出异常，不会requeue到队列，而是在消费者本地重试
 * 重试达到最大次数后，Spring会返回ack，相当于Spring默认本地重试一定次数后消息一定会被成功消费，消息会被丢弃
 *
 * 这个丢弃策略是默认的，但是这样就会造成消息丢失，并不好，因此我们需要有一个MessageRecovery接口来处理，这个接口包含3种实现：
 * RejectAndDontRequeueRecoverer：重试耗尽后，直接reject，丢弃消息。默认就是这种方式
 * ImmediateRequeueMessageRecoverer：重试耗尽后，返回nack，消息重新入队
 * RepublishMessageRecoverer：重试耗尽后，将失败消息投递到指定的交换机
 *
 * 我们选择RepublishMessageRecoverer，处理消息失败后将消息投递到一个指定的，专门存放异常消息的队列，后续由人工集中处理。
 * 配置就是本类。
 *
 * 当消费者出现异常了，异常消息的处理方案可以使用这种，也可以使用死信交换机那种。
 *
 * @author CHAN
 * @since 2022/7/8
 */
@Configuration
public class RepublishMessageRecovererConfig {
    /**
     * 专门存放处理失败的消息的交换机
     * @return
     */
    @Bean
    public DirectExchange errorMessageExchange(){
        return new DirectExchange("error.direct");
    }

    /**
     * 存放异常消息的队列
     * @return
     */
    @Bean
    public Queue errorQueue(){
        return new Queue("error.queue", true);
    }

    /**
     * 绑定
     * @return
     */
    @Bean
    public Binding errorBinding(){
        return BindingBuilder.bind(errorQueue()).to(errorMessageExchange()).with("error");
    }
    /**
     * 定义一个RepublishMessageRecoverer，关联队列和交换机
     */
    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }
}
