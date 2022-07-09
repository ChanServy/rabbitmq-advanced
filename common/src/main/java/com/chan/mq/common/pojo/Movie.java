package com.chan.mq.common.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author CHAN
 * @since 2022/7/8
 */
@Data
public class Movie implements Serializable {
    private int id;
    private String name;
    private String author;
}
