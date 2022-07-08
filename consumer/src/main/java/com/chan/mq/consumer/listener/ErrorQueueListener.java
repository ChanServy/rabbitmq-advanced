package com.chan.mq.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author CHAN
 * @since 2022/7/8
 */
@Slf4j
@Component
public class ErrorQueueListener {
    @RabbitListener(queues = {"error.queue"})
    public void listenDlQueue(String msg){
        log.info("接收到 error.queue 的错误消息：{}", msg);
    }
}
