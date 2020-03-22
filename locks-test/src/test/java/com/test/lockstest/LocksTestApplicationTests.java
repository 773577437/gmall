package com.test.lockstest;

import com.test.lockstest.service.LocksTestService;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.rowset.CachedRowSet;

@SpringBootTest
class LocksTestApplicationTests {

    @Autowired
    LocksTestService locksTestService;

    @Qualifier("mainThreadPoolExecutor")
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Test
    void contextLoads() {

//        locksTestService.redissonLocks();
        int corePoolSize = threadPoolExecutor.getCorePoolSize();
        System.out.println(corePoolSize);
    }

}
