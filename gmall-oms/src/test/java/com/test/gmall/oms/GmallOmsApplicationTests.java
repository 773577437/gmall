package com.test.gmall.oms;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.test.gmall.constant.OrderConstant;
import com.test.gmall.oms.entity.Order;
import com.test.gmall.oms.entity.OrderItem;
import com.test.gmall.oms.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.Queue;

@SpringBootTest
class GmallOmsApplicationTests {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    Queue queue;

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    @Test
    void contextLoads() {
        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_sn", "202003191154079241240486686819999746"));
        System.out.println(order.getPayAmount());
    }

    @Test
    void test11(){
        OrderItem orderItem = new OrderItem();
        orderItem.setProductSkuId(100L);
        orderItem.setProductQuantity(10);
        jmsMessagingTemplate.convertAndSend(queue, JSON.toJSONString(orderItem));
    }


}
