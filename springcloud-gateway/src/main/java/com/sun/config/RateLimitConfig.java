package com.sun.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RateLimitConfig {
    @Primary
    @Bean(value = "ipKeyResolver")
    KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }

    /**
     * API限流
     * @return
     * @author Damon
     * @date 2020年3月18日
     *
     */
    @Bean(value = "apiKeyResolver")
    KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }

    /**
     * 请求路径中必须携带userId参数
     * 用户限流
     * @return
     * @author Damon
     * @date 2020年3月18日
     *
     */
    @Bean(value = "userKeyResolver")
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("userId"));
    }

}