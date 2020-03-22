package com.test.lockstest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自定义属性可以在application.properties配置文件配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "thread.pool")
public class ThreadPoolProperties {

    /*核心线程池大小*/
    private Integer corePoolSize;
    /*线程池最大数*/
    private  Integer maximumPoolSize;
    /*队列大小*/
    private Integer queueSize;
}
