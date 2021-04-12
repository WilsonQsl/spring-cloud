package com.sun.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class TestController {
    @GetMapping("/userInfo")
    public String testUser(String userId){
        return "用户信息111"+userId;
    }

}
