package com.sun;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wilson
 */
@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String sayHello(String name){
        return "hello"+name;
    }
}
