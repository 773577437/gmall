package com.test.shop;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.test.gmall.pms.entity.Product;
import com.test.gmall.pms.entity.SkuStock;
import com.test.gmall.pms.service.ProductService;
import com.test.gmall.pms.service.SkuStockService;
import com.test.gmall.to.es.EsProduct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class GmallShopWebApplicationTests {


    @Reference
    ProductService productService;
    @Reference
    SkuStockService skuStockService;
    @Test
    void contextLoads() throws ExecutionException, InterruptedException {
//        ExecutorService pool = Executors.newFixedThreadPool(0);
//        CompletableFuture<String> uCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            SkuStock byId = skuStockService.getById(120);
//            return byId.getProductId();
//        },pool).thenApplyAsync((id)->{
//            Product product = productService.getById(id);
//            return JSON.toJSONString(product);
//        });
//
//        System.out.println(uCompletableFuture.get());

        SkuStock byId = skuStockService.getById(146);
        System.out.println(byId);
    }
    @Test
    void test(){
        EsProduct esProduct = productService.productInfoEsSkuById(98L);
        System.out.println(JSON.toJSONString(esProduct.getSkuInfoList()));
    }

    @Test
    void test1(){

    }
}
