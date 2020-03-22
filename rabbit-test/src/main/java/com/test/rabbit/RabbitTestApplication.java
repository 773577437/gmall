package com.test.rabbit;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

/**
 * 消息队列： rabbitMQ
 * 1、导包：
*                  <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-amqp</artifactId>
 *         </dependency>
 *  2、配置：
            spring.rabbitmq.host=192.168.1.70
            #账户，密码，默认 guest
            spring.rabbitmq.username=guest
            spring.rabbitmq.password=guest
            #连接范围，/ 代表连接所有
            spring.rabbitmq.virtual-host=/
 *  3、开启功能：@EnableRabbit
 *  4、自定义JSON格式数据传输
 *   @Bean
 *     public MessageConverter messageConverter(){
 *        return new Jackson2JsonMessageConverter();
 *     }
 *  操作对象 ： RabbitTemplate
 *
 */

/**
 * 消息队列： activemq
 * 1、导包
 *         <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-activemq</artifactId>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.apache.activemq</groupId>
 *             <artifactId>activemq-pool</artifactId>
 *             <version>5.15.11</version>
 *         </dependency>
 *  2、配置、看配置文件
 *  3、开启消息队列 @EnableJms
 *
 *  操作对象 JmsMessagingTemplate jmsMessagingTemplate
 */
@EnableJms
//@EnableRabbit
@SpringBootApplication
public class RabbitTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitTestApplication.class, args);
    }

}
