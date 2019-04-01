package com.sun.action;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerAction  {
	//@RequestMapping("/hello")
	public String hello(String name){
		return "hello!"+name;
	}
}
