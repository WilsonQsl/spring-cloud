package com.sun.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys")
public class TestController {
    @GetMapping("/sysInfo")
    public String testSysInfo(){
        return "系统信息";
    }
}
