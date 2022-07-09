package com.chan.mq.common.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 生产者消费者服务间传递的消息是对象时，对象的引用路径必须一致
 * 这样就得将这个对象抽取出来放到公共模块
 * 如果不这样，那么在发送消息之前，生产者可以将这个对象变成JSON字符串
 * 然后再发送，那么在消费者收到消息之后，需要将JSON字符串再转成对象。
 *
 * @author CHAN
 * @since 2022/7/7
 */
@Data
public class Book implements Serializable {
    private String name;
    private String author;
}
