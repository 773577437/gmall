package com.test.ssoserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//、配置redis
// *      spring.redis.host=192.168.1.70
// *      spring.redis.database=0
// *      spring.redis.port=6379
// *  使用 StringRedisTemplate 操作字符串
// *  使用 RedisTemplate<Object, Object>  操作对象
// *  可以自定义序列化器，使用json序列化器可以跨语言传递数据
// *      @Bean("redisTemplate")
// *     public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
// *             throws UnknownHostException {
// *         RedisTemplate<Object, Object> template = new RedisTemplate<>();
// *         template.setConnectionFactory(redisConnectionFactory);
// *         //修改默认序列化方式
// *         template.setDefaultSerializer( new GenericJackson2JsonRedisSerializer());
// *         return template;
// *     }
@SpringBootApplication
public class SsoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoServerApplication.class, args);
    }

}
