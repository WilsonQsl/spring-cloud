package com.sun.server;
import org.springframework.cloud.openfeign.FeignClient;
import com.sun.api.IHelloService;
import com.sun.config.HelloServiceHystrix;
@FeignClient(name= "producer",fallback = HelloServiceHystrix.class)
public interface ICHelloService extends IHelloService {
	/*@RequestMapping("/hello")
	public String hello(@RequestParam(value ="name")String name);*/
}
