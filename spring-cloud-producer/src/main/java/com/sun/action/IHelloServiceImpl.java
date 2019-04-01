package com.sun.action;

import org.springframework.web.bind.annotation.RestController;

import com.sun.api.IHelloService;
@RestController
public class IHelloServiceImpl implements IHelloService {

	@Override
	public String hello(String name) {
		return "hello!"+name+"!I am the first producer";
	}
}
