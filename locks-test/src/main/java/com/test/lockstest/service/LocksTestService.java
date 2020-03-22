package com.test.lockstest.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.executor.RedissonClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LocksTestService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    JedisPool jedisPool;

    @Autowired
    RedissonClient redisson;

    public void redissonLocks(){
        /*获取一把锁*/
        RLock lock = redisson.getLock("lock");
        try {
            /*加锁,3秒后自动解锁*/
            lock.lock(3, TimeUnit.SECONDS);
            Jedis jedis = jedisPool.getResource();
            /*方法被调用一次，redis缓存中就加一个值*/
            String numString = jedis.get("num");
            Integer num = Integer.parseInt(numString);
            num += 1;
            jedis.set("num",num.toString());
            jedis.close();
        } finally {
            /*解锁*/
            lock.unlock();
        }

    }

    public void cocksDistribute(){

        /**
         * 进程内加锁
         * 每次访问该代码块，都需要new一次()里面的对象,该对象的单例时，可以锁住
         * 1)、synchronized (stringRedisTemplate) spring自动注入对象是单例的 ---> 能锁
         * 2）、synchronized (this) 当前service对象是spring的组件也是单例的 --->能锁
         * 3）、synchronized (new Object()) ---> 不能锁
         */
//            synchronized (this){
//            /*方法被调用一次，redis缓存中就加一个值*/
//            ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
//            Long num = opsForValue.increment("num");
//            if(num != null){
//                num += 1;
//                opsForValue.set("num",num.toString());
//            }

        /**
         * 分布式加锁
         */
//        String token = UUID.randomUUID().toString();
//        /*setIfAbsent 判断有没有占坑 */
//        Boolean locks = redisTemplate.opsForValue().setIfAbsent("locks", token, 3, TimeUnit.SECONDS);
//        if(locks){
//            /*方法被调用一次，redis缓存中就加一个值*/
//            ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
//            Long num = opsForValue.increment("num");
//            if(num != null){
//                num += 1;
//                opsForValue.set("num",num.toString());
//             }
//
//            /*删除锁*/
//            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//            redisTemplate.execute(new DefaultRedisScript(script), Arrays.asList("locks"),token);
//
//            }else {
//            /*有人占坑，则自旋(重复调用该方法)*/
//            cocksDistribute();
//        }

        /**
         * 分布式加锁
         */
        Jedis jedis = jedisPool.getResource();
        try {
            String token = UUID.randomUUID().toString();
            SetParams setParams = SetParams.setParams().ex(3).nx();
            String locks = jedis.set("locks", token, setParams);
            /*如果占位成功，就会返回 ok（不区分大小写）*/
            if(locks != null && locks.equalsIgnoreCase("OK")){
                /*方法被调用一次，redis缓存中就加一个值*/
                String num1 = jedis.get("num");
                Integer num = Integer.parseInt(num1);
                num += 1;
                jedis.set("num",num.toString());

                /*删除锁*/
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script, Collections.singletonList("locks"), Collections.singletonList(token));

            }else{
                /*有人占坑，则自旋(重复调用该方法)*/
                cocksDistribute();
            }
        } finally {
            jedis.close();
        }

    }
}
