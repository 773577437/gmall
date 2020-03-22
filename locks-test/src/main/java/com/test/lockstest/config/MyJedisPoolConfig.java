package com.test.lockstest.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class MyJedisPoolConfig {

    /**
     * JedisConnectionFactory factory 这个参数是从容器中自动获取的
     * @return 将我们做好的 JedisPool 放在容器中
     */
    @Bean
    public JedisPool jedisPoolConfig(RedisProperties properties) throws Exception {
        //1、连接工厂中所有信息都有。
        JedisPoolConfig config = new JedisPoolConfig();
        RedisProperties.Pool pool = properties.getJedis().getPool();

        config.setMaxIdle(pool.getMaxIdle());
        config.setMaxTotal(pool.getMaxActive());

        JedisPool jedisPool = new JedisPool(config, properties.getHost(), properties.getPort());
        return jedisPool;
    }
}
