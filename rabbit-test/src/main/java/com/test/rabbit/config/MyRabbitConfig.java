package com.test.rabbit.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class MyRabbitConfig {

    /**
     * 自定义rabbit发送Json数据格式
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
       return new Jackson2JsonMessageConverter();
    }
}
