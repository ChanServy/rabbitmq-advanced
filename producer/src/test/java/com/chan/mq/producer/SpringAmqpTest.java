package com.chan.mq.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author CHAN
 * @since 2022/7/7
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAmqpTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * basic queue 简单队列模型
     * 利用RabbitTemplate实现向指定队列中发送消息。
     * 一个生产者向队列中推送消息，一个消费者监听队列消费消息。
     */
    @Test
    public void testSimpleQueue() {
        // 队列名称
        String queueName = "simple.queue";
        // 消息
        String message = "hello, spring amqp!";
        // 发送消息
        rabbitTemplate.convertAndSend(queueName, message);
    }

    /**
     * WorkQueues，也被称为（Task queues），任务模型
     * 向队列中不停发送消息，模拟消息堆积。
     * 一个生产者向队列中推送消息，让多个消费者绑定到一个队列，共同消费队列中的消息。避免消息堆积。
     */
    @Test
    public void testWorkQueue() throws InterruptedException {
        // 队列名称
        String queueName = "work.queue";
        // 消息
        String message = "hello, message_";
        for (int i = 0; i < 10; i++) {
            // 发送消息
            rabbitTemplate.convertAndSend(queueName, message + i);
            Thread.sleep(20);
        }
    }

}
