package com.test.lockstest.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 只对于那些获得修改共享数据的操作加锁，无关的耗时操作不加锁
 * 常用读写锁
 */
@Service
public class RedisssonService {

    @Autowired
    RedissonClient redisson;

    /*1. 可重入锁*/
    public void reentrantLock() throws InterruptedException {
        RLock lock = redisson.getLock("anyLock");
        // 最常见的使用方法
//        lock.lock();

        // 加锁以后10秒钟自动解锁
        // 无需调用unlock方法手动解锁
//        lock.lock(10, TimeUnit.SECONDS);

        // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
        boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
        if (res) {
            try {
                /*要加锁的代码块*/
//              ...
            } finally {
                lock.unlock();
            }
        }
    }

}
