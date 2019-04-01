package com.sun.config;
import org.springframework.stereotype.Component;
import com.sun.server.ICHelloService;
@Component
public class HelloServiceHystrix implements ICHelloService {

	@Override
	public String hello(String name) {
		return "请求失败";
	}
}
