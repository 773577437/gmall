package com.test.lockstest.controller;

import com.test.lockstest.service.ThreadTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ThreadPoolController {

    @Autowired
    private ThreadTestService threadTestService;

    @GetMapping("/thread")
    public String threadPool(){
//        List<String> strings = threadTestService.threadPool();
//        return  strings.toString();
        return  threadTestService.threadPool();
    }
}
