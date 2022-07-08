package com.chan.mq.consumer.listener;

import com.chan.mq.common.pojo.Book;
import com.chan.mq.common.pojo.Movie;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 监听消息：使用@RabbitListener
 * @RabbitListener ：加在类或者方法上（监听哪些队列即可）
 * @RabbitHandler ：加在方法上（区分不同类型的消息）
 * 区分的不同类型消息：
 * 可能是listener监听两个队列的消息，两个队列中的消息类型可能不同，String、Book。
 * 也可能是listener监听一个队列中的消息，发送到这个队列的多条消息类型可能不同，String、Book。
 *
 * @author CHAN
 * @since 2022/7/7
 */
// @RabbitListener(queues = {"simple.queue", "work.queue"})
@Component
@RabbitListener(queues = {"simple.queue"})
public class SimpleQueueListener {
    /**
     * 监听simple.queue队列中的消息，如果simple.queue不存在会报错。
     * // @RabbitListener(queues = "simple.queue")
     * // public void listenSimpleQueueMessage(String msg) throws InterruptedException {
     * //     System.out.println("spring 消费者接收到消息：【" + msg + "】");
     * // }
     */

    @RabbitHandler
    public void listenSimpleQueueMessage(String msg) {
        System.out.println("spring 消费者接收到String类型的消息：【" + msg + "】");
    }

    @RabbitHandler
    public void listenSimpleQueueMessage(Book book) {
        System.out.println("spring 消费者接收到Book类型的消息：【" + book + "】");
    }

    @RabbitHandler
    public void listenSimpleQueueMessage(Movie movie) {
        System.out.println("spring 消费者接收到Movie类型的消息：【" + movie + "】");
    }
}
