package com.sun;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
@Component
public class HelloRemoteHystrix implements HelloService {

    @Override
    public String hello(@RequestParam(value = "name") String name) {
        return name+"请求服务失败";
    }
}
