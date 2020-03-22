package com.test.gmall.pms;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * redis 两大步
 * 1）、导入redis场景
 *         <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-data-redis</artifactId>
 *         </dependency>
 * 2）、配置redis
 *      spring.redis.host=192.168.1.70
 *      spring.redis.database=0
 *      spring.redis.port=6379
 *  使用 StringRedisTemplate 操作字符串
 *  使用 RedisTemplate<Object, Object>  操作对象
 *  可以自定义序列化器，使用json序列化器可以跨语言传递数据
 *      @Bean("redisTemplate")
 *     public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
 *             throws UnknownHostException {
 *         RedisTemplate<Object, Object> template = new RedisTemplate<>();
 *         template.setConnectionFactory(redisConnectionFactory);
 *         //修改默认序列化方式
 *         template.setDefaultSerializer( new GenericJackson2JsonRedisSerializer());
 *         return template;
 *     }
 *  如果添加的事务没效果，则开启事务控制功能  @EnableTransactionManagement
 *  事务的最终方案：
 *      1）、普通事务：导jdbc-starter，开启事务注解功能 @EnableTransactionManagement，添加事务 @Transactional
 *      2）、方法自己调自己类里面的的加不上事务；
 *          1）、加 <artifactId>spring-boot-starter-aop</artifactId> aop包
 *          2）、获取当前类的代理对象，去掉方法即可；
 *              @EnableAspectJAutoProxy(exposeProxy = true):暴露代理对象
 *              获取当前类代理对象
 *              当前类 proxy=(当前类)AopContext.currentProxy();
 */
@EnableJms
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableTransactionManagement
@EnableDubbo
@MapperScan("com.test.gmall.pms.mapper")
@SpringBootApplication
public class GmallPmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPmsApplication.class, args);
    }

}
