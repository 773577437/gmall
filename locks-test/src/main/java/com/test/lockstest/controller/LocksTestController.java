package com.test.lockstest.controller;

import com.test.lockstest.service.LocksTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocksTestController {

    @Autowired
    LocksTestService locksTestService;

    @GetMapping("/locks")
    public String test(){
//        locksTestService.cocksDistribute();
        locksTestService.redissonLocks();
        return "ok";
    }
}
