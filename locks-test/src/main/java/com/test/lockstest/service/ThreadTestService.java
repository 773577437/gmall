package com.test.lockstest.service;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 多线程测试
 */
@Service
public class ThreadTestService {

    //可以返回结果的异步线程
//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        //创建可以存储10个线程数的线程池
//        ExecutorService pool = Executors.newFixedThreadPool(10);
//        System.out.println("主线程开始。。。。");
//        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("异步线程开始。。。。" + Thread.currentThread());
//            String s = UUID.randomUUID().toString();
//            System.out.println("异步线程结束。。。。" + Thread.currentThread());
//            return s;
//        }, pool).thenApply((r)->{
//            //修改返回结果
//            return r.replace("-", "");
//        });
//        future.whenComplete((r,e)->{
//            System.out.println("返回结果。。。。" + r);
//            System.out.println("返回异常。。。。" + e);
//        });
//
////        System.out.println("返回结果。。。。" + future.get());
//        System.out.println("主线程结束。。。。");
//    }

    @Qualifier("mainThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    //先返回主线程，然后异步线程
    public String  threadPool(){
//        ExecutorService pool = Executors.newFixedThreadPool(10);
        System.out.println("主线程开始。。。。");
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("查询商品品牌");
            return "苹果";
        }, threadPoolExecutor).whenComplete((r, e) -> {
            System.out.println(r);
        });
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("查询商品属性");
            return "手机";
        }, threadPoolExecutor).whenComplete((r, e) -> {
            System.out.println(r);
        });
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("查询商品标题");
            return "苹果手机";
        }, threadPoolExecutor).whenComplete((r, e) -> {
            System.out.println(r);
        });
        String s = null;
        try {
            s = f1.get();
            s += f2.get();
            s += f3.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return s;
    }

}
