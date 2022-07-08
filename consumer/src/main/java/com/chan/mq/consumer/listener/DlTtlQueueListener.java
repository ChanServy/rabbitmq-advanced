package com.chan.mq.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author CHAN
 * @since 2022/7/8
 */
@Component
@Slf4j
public class DlTtlQueueListener {
    @RabbitListener(queues = {"dl.ttl.queue"})
    public void listenDlQueue(String msg){
        log.info("接收到 dl.ttl.queue的延迟消息：{}", msg);
    }
}
