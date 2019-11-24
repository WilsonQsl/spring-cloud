package com.sun;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wilson
 */
@FeignClient(name= "eureka-client",fallback = HelloRemoteHystrix.class)
public interface HelloService {
    /**
     * say hello
     * @param name
     * @return
     */
    @RequestMapping("/hello")
    String hello(@RequestParam(value = "name") String name);
}
