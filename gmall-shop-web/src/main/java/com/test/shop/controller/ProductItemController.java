package com.test.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.test.gmall.pms.service.ProductService;
import com.test.gmall.search.SearchProductService;
import com.test.gmall.to.es.EsProduct;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@Api("详情信息")
public class ProductItemController {

    @Reference
    ProductService productService;

    @GetMapping("/item/{id}.html")
    public EsProduct productInfo(@PathVariable("id") Long id){
        /*根据商品id，获取商品详情*/
        EsProduct esProduct = productService.productInfoEsById(id);
        return esProduct;
    }

    @GetMapping("/item/sku/{id}.html")
    public EsProduct productInfoSku(@PathVariable("id") Long id){
        /*根据商品sku的id，获取商品详情*/
        EsProduct esProduct = productService.productInfoEsSkuById(id);
        return esProduct;
    }

}
