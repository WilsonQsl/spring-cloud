package com.sun;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * @author wilson
 */
@SpringBootApplication  //启动类
@EnableDiscoveryClient  //注册中心
@EnableFeignClients     //打开Feign注解
@EnableCircuitBreaker   //打开Hystrix断路器
@EnableHystrixDashboard //开启hystrix的熔断监控
public class EurekaConsumeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumeApplication.class, args);
    }
    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet); registrationBean.setLoadOnStartup(1); registrationBean.addUrlMappings("/actuator/hystrix.stream"); registrationBean.setName("HystrixMetricsStreamServlet"); return registrationBean;
    }

}
