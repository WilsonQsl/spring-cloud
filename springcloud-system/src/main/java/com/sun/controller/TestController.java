package com.sun.controller;

import com.sun.feign.UserFeignFallback;
import com.sun.feign.UserFeignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sys")
public class TestController {
    @Resource
    UserFeignService userFeignService;
    @GetMapping("/sysInfo")
    public String testSysInfo(){
        return "系统信息";
    }

    @GetMapping("/getUserId")
    public String testUser(String userId){
        return userFeignService.testUser(userId);
    }
}
