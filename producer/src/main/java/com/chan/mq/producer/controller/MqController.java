package com.chan.mq.producer.controller;

import cn.hutool.json.JSONUtil;
import com.chan.mq.common.pojo.Book;
import com.chan.mq.common.pojo.Movie;
import com.chan.mq.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * @author CHAN
 * @since 2022/7/7
 */
@RestController
@Slf4j
public class MqController {

    @Resource
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/simple")
    public R simpleSendToQueue() {
        Book book = new Book();
        book.setName("蛤蟆先生去看心理医生");
        book.setAuthor("罗伯特");
        String bookJson = JSONUtil.toJsonStr(book);
        rabbitTemplate.convertAndSend("simple.queue", bookJson);
        return R.ok();
    }

    @RequestMapping("/work")
    public R workSendToQueue() {
        try {
            // 队列名称
            String queueName = "work.queue";
            // 消息
            String message = "hello, message_";
            for (int i = 0; i < 10; i++) {
                // 发送消息
                rabbitTemplate.convertAndSend(queueName, message + i);
                Thread.sleep(20);
            }
            return R.ok();
        } catch (InterruptedException e) {
            log.error("error:" + e);
            return R.error();
        }
    }

    @RequestMapping("/fanout")
    public R fanoutSendToExchange() {
        // 交换机名称
        String exchangeName = "fanout.exchange";
        // 消息
        String message = "hello, Rabbit FanoutExchange!";
        rabbitTemplate.convertAndSend(exchangeName, "", message);
        return R.ok();
    }

    @RequestMapping("/direct")
    public R directSendToExchange() {
        // 交换机名称
        String exchangeName = "direct.exchange";
        // 消息
        String message = "红色警报！日本乱排核废水，导致海洋生物变异，惊现哥斯拉！";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "blue", message);
        return R.ok();
    }

    @RequestMapping("/topic")
    public R topicSendToExchange() {
        // 交换机名称
        String exchangeName = "topic.exchange";
        // 消息
        String message = "喜报！孙悟空大战哥斯拉，胜!";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "china.news", message);
        return R.ok();
    }

    @RequestMapping("/confirm")
    public R testSendWithConfirmCallback() {
        // 消息体
        Movie movie = new Movie();
        movie.setId(1);
        movie.setName("复仇者联盟");
        movie.setAuthor("漫威");
        // String movieJson = JSONUtil.toJsonStr(movie);
        // 全局唯一的消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(String.valueOf(movie.getId()));
        // 添加callback
        correlationData.getFuture().addCallback(result -> {
            // 判断结果
            if (Objects.requireNonNull(result).isAck()) {
                // ACK
                log.debug("消息成功投递到交换机！消息ID: {}", correlationData.getId());
            } else {
                // NACK
                log.error("消息投递到交换机失败！消息ID：{}，原因：{}", correlationData.getId(), result.getReason());
                // 重发消息
            }
        }, exception -> {
            // 记录日志
            log.error("消息发送异常, 消息ID：{}，原因：{}", correlationData.getId(), exception.getMessage());
            // 重发消息
        });
        // 发送消息
        rabbitTemplate.convertAndSend("simple.queue", movie, correlationData);
        return R.ok();
    }

    @RequestMapping("/ttlqueue")
    public R testSendToTTLQueue() {
        // 创建消息
        String message = "hello, ttl queue";
        // 消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 发送消息
        rabbitTemplate.convertAndSend("ttl.direct", "ttl", message, correlationData);
        // 记录日志
        log.debug("发送消息成功");
        return R.ok();
    }

    /**
     * 由于我们需要设置消息的TTL超时时间，因此需要使用MessageBuilder的方式发送消息。当使用MessageBuilder发送消息到MQ时，会有一个问题：
     * 就是当我们项目中同时配置了Jackson2JsonMessageConverter(将对象类型的消息序列化成JSON)，消息序列化时会出错导致异常。
     * 取消这个序列化配置，就不会出错了，但是取消序列化配置之后，发送对象类型的消息（比如消息体是一个Book类型）时又会报错。
     * 显示SimpleMessageConverter只能转换String、字节数组等基本类型，因此我们可以让这个对象实现Serializable；
     * 但是不配置JSON序列化，而使用Java的Serializable序列化的话，将对象消息发送到MQ服务器时可读性太差。
     * 因此，最好的方式是我们自己将对象序列化成JSON字符串之后，再将对象的JSON字符串发送到MQ，消费者收到JSON之后再将JSON序列化成对象包装类。
     * 简单来说就是如果我们想发送一个我们自己的对象类型到MQ，我们自己完成JSON序列化和反序列化，不用通过配置让MQ帮我们完成。
     * @return R
     */
    @RequestMapping("/ttlmsg")
    public R testSendTTLMsgToQueue() {
        // 创建消息
        Message message = MessageBuilder
                .withBody("hello, ttl message".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setExpiration("5000")
                .build();
        // 消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 发送消息
        rabbitTemplate.convertAndSend("normal.queue", message, correlationData);
        log.debug("发送消息成功");
        return R.ok();
    }

    @RequestMapping("/delay")
    public R testDelayQueue() {
        // 1.准备消息
        Book book = new Book();
        book.setName("蛤蟆先生去看心理医生");
        book.setAuthor("罗伯特");
        String bookJson = JSONUtil.toJsonStr(book);
        Message message = MessageBuilder
                .withBody(bookJson.getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setHeader("x-delay", 5000)
                .build();
        // 2.准备CorrelationData
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 3.发送消息
        rabbitTemplate.convertAndSend("delay.direct", "delay", message, correlationData);

        log.info("发送消息成功");
        return R.ok();
    }
}
