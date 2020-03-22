package com.test.lockstest.config;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    /**
     * 自定义核心线程池
     * @param poolProperties
     * @return
     */
    @Bean("mainThreadPoolExecutor")
    public ThreadPoolExecutor mainThreadPoolExecutor(ThreadPoolProperties poolProperties){

        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(poolProperties.getQueueSize());
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(poolProperties.getCorePoolSize(), poolProperties.getMaximumPoolSize(),
                10, TimeUnit.MINUTES, queue);

        return threadPoolExecutor;
    }

    /**
     * 自定义非核心线程池
     * @param poolProperties
     * @return
     */
    @Bean("otherThreadPoolExecutor")
    public ThreadPoolExecutor otherThreadPoolExecutor(ThreadPoolProperties poolProperties){

        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(poolProperties.getQueueSize());
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(poolProperties.getCorePoolSize(), poolProperties.getMaximumPoolSize(),
                10, TimeUnit.MINUTES, queue);

        return threadPoolExecutor;
    }
}
