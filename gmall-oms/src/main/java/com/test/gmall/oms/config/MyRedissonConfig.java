package com.test.gmall.oms.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedissonConfig {

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
//        config.useClusterServers().addNodeAddress("redis://192.168.1.70:6379");
        /*单节点模式*/
        config.useSingleServer().setAddress("redis://192.168.1.70:6379");
        return Redisson.create(config);
    }
}
