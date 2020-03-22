package com.test.lockstest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

//@EnableAsync  开启异步线程功能， 实现 AsyncConfigurer接口，配置线程池， 然后注解异步方法@Async
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)  //导了mybatis包，却没有相关配置，一定要排除数据源自动配置类
public class LocksTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocksTestApplication.class, args);
    }


}
