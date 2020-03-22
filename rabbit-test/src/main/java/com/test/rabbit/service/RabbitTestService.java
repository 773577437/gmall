package com.test.rabbit.service;

import com.rabbitmq.client.Channel;
import com.test.rabbit.bean.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 消息队列：实现异步任务
 * 死信队列：实现延时任务
 *  @EnableScheduling  实现定时任务
 */

@Service
public class RabbitTestService {

    /**
     * 接收user队列消息
     * @param message  获取字节类型数据和消息的其他属性
     * @param user  获取明确的User对象数据
     * @param channel  通道，可以做拒绝接收操作
     */
//    @RabbitListener(queues = "my_queue_01")
    public void receiveMessage(Message message, User user, Channel channel) {
        //message.getMessageProperties() 获取的数据：MessageProperties [headers={__TypeId__=com.test.rabbit.bean.User},
        // contentType=application/json, contentEncoding=UTF-8, contentLength=0,
        // receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=exchange.topic,
        // receivedRoutingKey=user, deliveryTag=1, consumerTag=amq.ctag-ziq3QayXVXBanBZN_3FgrQ, consumerQueue=user]
//        System.out.println(message.getMessageProperties());
        //获取的消息：User{username='buer'}

        try {
//            int i = 10/0;   //当接收方法报错时队列数据不会自动删除，并且不断的接收和报错循环，
            System.out.println(user);
            //回复成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            try {
                //回复失败，拒绝接收数据，让 rabbitmq 转发给别的队列
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    //-------------------------------activemq----------------------------

    /**
     * 接收queue对应队列的消息
     * @param mgs
     */
    @JmsListener(destination = "my_queue", containerFactory = "jmsListenerContainerQueue")
    public void receiveQueue(String mgs){
        System.out.println(mgs);
    }

    /**
     * 接收topic(订阅)对应队列的消息
     * 这里通过传入不同的factory, 来实现发送不同类型的信息.
     * @param mgs
     */
    @JmsListener(destination = "my_topic", containerFactory = "jmsListenerContainerTopic")
    public void receiveTopic(String mgs){
        System.out.println(mgs);
    }


}
