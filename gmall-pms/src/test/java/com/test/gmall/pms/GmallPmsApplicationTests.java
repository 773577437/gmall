package com.test.gmall.pms;

import com.alibaba.fastjson.JSON;
import com.test.gmall.oms.entity.OrderItem;
import com.test.gmall.pms.entity.Brand;
import com.test.gmall.pms.mapper.ProductAttributeValueMapper;
import com.test.gmall.pms.service.ProductService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.Queue;

@RunWith(SpringRunner.class)
@SpringBootTest
class GmallPmsApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplateObject;

    @Autowired
    ProductAttributeValueMapper productAttributeValueMapper;

    @Autowired
    Queue queue;

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    @Reference
    ProductService productService;
    @Test
    void contextLoads() {
//        redisTemplate.opsForValue(): 操作string类型的数据
//        redisTemplate.opsForHash(); 操作map数据类型的
//        redisTemplate.opsForList(); 操作list数据类型的

//        redisTemplate.opsForValue().set("hello","你好");
//
//        String hello = redisTemplate.opsForValue().get("hello");
//        System.out.println(hello);

        //在redis 存储对象
        Brand brand = new Brand();
        brand.setName("不二");
        redisTemplateObject.opsForValue().set("bb", brand);

        //在redis 获取对象
        Object abc = redisTemplateObject.opsForValue().get("bb");
        System.out.println(abc.toString());
    }

    @Test
    void test11(){
        OrderItem orderItem = new OrderItem();
        orderItem.setProductSkuId(99L);
        orderItem.setProductQuantity(10);
        jmsMessagingTemplate.convertAndSend(queue, JSON.toJSONString(orderItem));
    }

}
