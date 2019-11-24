package com.sun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wilson
 */
@RestController
public class HelloConsume {
    @Autowired
    HelloService helloService;

    @RequestMapping("/hello")
    public String index(String name) {
        return helloService.hello(name);
    }
}
