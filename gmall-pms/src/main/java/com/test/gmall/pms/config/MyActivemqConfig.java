package com.test.gmall.pms.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
public class MyActivemqConfig {

    @Value("${queueName}")
    private String queueName;

//    @Value("${topicName}")
//    private String topicName;

    @Value("${spring.activemq.user}")
    private String usrName;

    @Value("${spring.activemq.password}")
    private  String password;

    @Value("${spring.activemq.broker-url}")
    private  String brokerUrl;

    @Bean
    public ActiveMQQueue queue(){
        return new ActiveMQQueue(queueName);
    }

//    @Bean
//    public ActiveMQTopic topic(){
//        return new ActiveMQTopic(topicName);
//    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(usrName, password, brokerUrl);
//        factory.setTrustedPackages(new ArrayList(Arrays.asList("org.apache.activemq.test,org.apache.camel.test".split(","))));
//        factory.setTrustAllPackages(true);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ActiveMQConnectionFactory connectionFactory){
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(connectionFactory);
        return bean;
    }

//    @Bean
//    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ActiveMQConnectionFactory connectionFactory){
//        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
//        //设置为发布订阅方式, 默认情况下使用的生产消费者方式
//        bean.setPubSubDomain(true);
//        bean.setConnectionFactory(connectionFactory);
//        return bean;
//    }
}
