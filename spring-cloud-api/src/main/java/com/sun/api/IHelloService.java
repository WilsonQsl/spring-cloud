package com.sun.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * 声明服务提供者接口
 * @author Administrator 2018/10/08
 *
 */
public interface IHelloService {
	@RequestMapping("/hello")
	public String hello(@RequestParam(value ="name")String name);
}
