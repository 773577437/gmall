package com.test.rabbit;

import com.test.rabbit.bean.User;
import com.test.rabbit.component.CreateRabbitmqComponent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.jms.Queue;
import javax.jms.Topic;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@SpringBootTest
class RabbitTestApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CreateRabbitmqComponent createRabbitmqComponent;  //自定义组件

    //activemq
    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    Queue queue;

    @Autowired
    Topic topic;


    @Test
    void contextLoads() {

        /**
         *  参数：交换机名，路由器，发送数据
         *  路由键：交换机和消息队列的发送规则
         *  交换机常用模式：
         *  direct: 点对点模式; 交换机添加消息队列，发送数据时路由键名称对应就行
         *  fanout: 订阅模式； 交换机添加消息队列，只要添加了的消息队列，自动发送
         *  topic: 匹配模式： 按照交换机添加信息队列时，填写的路由键规则，决定发送
         *  *，代表任意一给单词，# 代表任意0或多个单词，
         *  例如路由键名是 user.news  则发送给对应路由键规则为 user.# 和 #.news 的信息队列
         */
        User user = new User("buer");
        //使用exchange.topic交换机通过user路由键发送消息
        rabbitTemplate.convertAndSend("my_delay_exchange", "my_delay", user);
    }

    @Test
    void test(){
        //创建队列
        createRabbitmqComponent.createQueue("my_queue_01", null);
        //创建交换机
        createRabbitmqComponent.createExchange("my_direct_exchange", "direct");
        //创建绑定关系
        createRabbitmqComponent.createBinding("my_queue_01","my_direct_exchange","my01");
    }

    @Test
    void test1(){   //死信队列功能：可以实现延迟任务

        //设置死信队列消息过期过期时间和过期后交给那个交换机处理
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-message-ttl",10*1000);          //过期时间
        arguments.put("x-dead-letter-exchange","my_direct_exchange"); //消息过期后交给的交换机
        arguments.put("x-dead-letter-routing-key","my01"); //死信发出的路由键

        //创建死信队列
        createRabbitmqComponent.createQueue("my_delay_queue",arguments);
        //创建交换机
        createRabbitmqComponent.createExchange("my_delay_exchange", "direct");
        //创建绑定关系
        createRabbitmqComponent.createBinding("my_delay_queue","my_delay_exchange","my_delay");
    }

    @Test
    void test2(){
        //发送queue消息
        jmsMessagingTemplate.convertAndSend(queue, "hello word!");
    }

    @Test
    void test3(){
        //发送topic订阅消息
        jmsMessagingTemplate.convertAndSend(topic, "hello word!");
    }


    //==============Spring定时任务，手动关闭开启=====================================

    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;   //线程池任务调度类，能够开启线程池进行任务调度。
    private ScheduledFuture<?> future;

    //开启定时任务
    @Test
    void startCron(){
        //Runnable（线程接口类） 和CronTrigger（定时任务触发器）
        future = threadPoolTaskScheduler.schedule(new MyRunnable(), new CronTrigger("5/30 * * * * ? "));   //5秒后每隔30秒，执行该方法
    }

    //关闭定时任务
    @Test
    void stopCron(){
        future.cancel(true);
    }

    //修改定时任务
    @Test
    void updateCron(){
        //先关闭，再开启
        stopCron();
        //Runnable（线程接口类） 和CronTrigger（定时任务触发器）
        future = threadPoolTaskScheduler.schedule(new MyRunnable(), new CronTrigger("5/10 * * * * ? "));   //5秒后每隔30秒，执行该方法
    }

    private class MyRunnable implements Runnable {

        @Override
        public void run() {
            System.out.println("DynamicTask.MyRunnable.run()，" + new Date());
        }
    }

}

