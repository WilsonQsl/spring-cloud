package com.sun.feign;

import org.springframework.stereotype.Service;

@Service
public class UserFeignFallback implements UserFeignService {
    @Override
    public String testUser(String userId) {
        return "系统错误";
    }
}
