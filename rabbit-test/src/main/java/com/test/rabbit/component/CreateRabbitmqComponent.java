package com.test.rabbit.component;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreateRabbitmqComponent {

    @Autowired
    private AmqpAdmin amqpAdmin;

    /**
     * 创建队列
     * @param queueName  队列名
     */
    public void createQueue(String queueName, Map<String, Object> arguments){

        /**
         * String name, boolean durable,  是否持久化
         * boolean exclusive,  是否排外
         * boolean autoDelete  是否自动删除
         * @Nullable Map<String, Object> arguments 参数
         */
        Queue queue = new Queue(queueName, true, false, false, arguments);
        amqpAdmin.declareQueue(queue);
    }

    /**
     * 创建交换机
     * @param exchangeName  交换机名
     * @param exchangeType  交换机类型
     */
    public void createExchange(String exchangeName, String exchangeType){

        /**
         * String name, boolean durable, 是否持久化
         * String getType();  交换机类型
         * boolean autoDelete  是否自动删除
         */
        Exchange exchange = new CustomExchange(exchangeName, exchangeType,true,false);
        amqpAdmin.declareExchange(exchange);
    }

    /**
     * 创建绑定关系
     * @param queueName
     * @param exchangeName
     * @param routingKey  路由键
     */
    public void createBinding(String queueName, String exchangeName, String routingKey){

        /**
         * String destination, //队列名
         * Binding.DestinationType destinationType,  //绑定类型
         * String exchange,  //交换机名
         * String routingKey,  //路由键
         * @Nullable Map<String, Object> arguments  //参数
         */
        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, null);
        amqpAdmin.declareBinding(binding);
    }
}
