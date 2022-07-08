package com.chan.mq.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 创建并监听延迟队列
 *
 * 延时队列可以使用这种，也可以使用死信交换机那种。
 *
 * @author CHAN
 * @since 2022/7/8
 */
@Component
@Slf4j
public class DelayQueueListener {
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "delay.queue", durable = "true"),
            exchange = @Exchange(name = "delay.direct", delayed = "true"),
            key = "delay"
    ))
    public void listenDelayExchange(String msg) {
        log.info("消费者接收到了delay.queue的延迟消息:{}", msg);
    }
}
