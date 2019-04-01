package com.sun.action;

import javax.annotation.Resource;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloAction {
	
	@Resource
	private LoadBalancerClient loadBalancer;
	@Resource
	private DiscoveryClient discoveryClient;

	/**
	 * 获取服务的基本信息
	 */
	@RequestMapping("/services")
	public Object services() {
		return discoveryClient.getInstances("spring-consumer");
	}

	/**
	 * 查看所有的服务
	 * 
	 * @return
	 */
	@RequestMapping("/getServices")
	public Object getServices() {
		return this.discoveryClient.getServices();
	}

	/**
	 * 从所有服务中选择一个服务（轮询）
	 */
	@RequestMapping("/discover")
	public Object discover() {
		return loadBalancer.choose("spring-consumer").getUri().toString();
	}

	/**
	 * 
	 * @return 实例的所有信息
	 */
	@RequestMapping("/id")
	public ServiceInstance getService() {
		return this.loadBalancer.choose("spring-consumer");
	}

	/**
	 * 查看实例的基本信息
	 */
	@RequestMapping("/info")
	public void printInfo() {
		ServiceInstance instanace = this.loadBalancer.choose("spring-consumer");
		System.out.println("实例的ID：" + instanace.getServiceId());
		System.out.println("实例主机：" + instanace.getHost());
		System.out.println("实例端口号：" + instanace.getPort());
		System.out.println("实例资源路径：" + instanace.getUri());
	}

}
