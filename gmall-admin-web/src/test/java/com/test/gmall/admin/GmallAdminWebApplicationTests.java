package com.test.gmall.admin;

import com.alibaba.dubbo.config.annotation.Reference;
import com.test.gmall.pms.entity.Brand;
import com.test.gmall.pms.service.BrandService;
import com.test.gmall.pms.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallAdminWebApplicationTests {

    @Reference
    private BrandService brandService;

    @Test
    void contextLoads() {
        Brand byId = brandService.getById(1);
        System.out.println(byId);
    }

}
