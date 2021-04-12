package com.sun.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service-api",fallback = UserFeignFallback.class)
public interface UserFeignService {
    @GetMapping("/user/userInfo")
    String testUser(@RequestParam("userId") String userId);
}
