package com.sun.action;

import java.net.URI;
import javax.annotation.Resource;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.sun.server.ICHelloService;

@RestController
public class ConsumerAction {
	@Resource
	private LoadBalancerClient loadBalancer;
	@Resource
	private RestTemplate restTemplate;
	@Resource
	private ICHelloService helloService;
	
	@RequestMapping("/sayhello")
	public String sayHello(String name){
		URI uri = loadBalancer.choose("producer").getUri();
		String url = uri+"/hello?name="+name;
		System.out.println(url);
		return this.restTemplate.getForObject(url, String.class);
	}
	@RequestMapping("/hello")
	public String hello(String name){
		return this.helloService.hello(name);
	}
}
