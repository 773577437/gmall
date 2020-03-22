package com.test.gmall.admin.ums.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.test.gmall.ums.entity.MemberLevel;
import com.test.gmall.ums.service.MemberLevelService;
import com.test.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会员等级管理
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/memberLevel")
public class UmsMemberLevelController {

    @Reference
    private MemberLevelService memberLevelService;

    @GetMapping("/list")
    public Object getMemberLevelList(){
        List<MemberLevel> list = memberLevelService.list();
        return new CommonResult().success(list);
    }
}
