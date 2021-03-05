package com.sun.filter;/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * User Request Rate Limiter filter. See https://stripe.com/blog/rate-limiters and
 * 重写 RequestRateLimiterGatewayFilterFactory
 */
@ConfigurationProperties("spring.cloud.gateway.filter.request-rate-limiter")
@Component("CustomRequestRateLimiter")
public class CustomRequestRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomRequestRateLimiterGatewayFilterFactory.Config> {

    private static Logger logger = LoggerFactory.getLogger(CustomRequestRateLimiterGatewayFilterFactory.class);
    /**
     * Key-Resolver key.
     */
    public static final String KEY_RESOLVER_KEY = "keyResolver";

    private static final String EMPTY_KEY = "____EMPTY_KEY__";

    private final RateLimiter defaultRateLimiter;

    private final KeyResolver defaultKeyResolver;

    /**
     * Switch to deny requests if the Key Resolver returns an empty key, defaults to true.
     */
    private boolean denyEmptyKey = true;

    /**
     * HttpStatus to return when denyEmptyKey is true, defaults to FORBIDDEN.
     */
    private String emptyKeyStatusCode = HttpStatus.FORBIDDEN.name();

    public CustomRequestRateLimiterGatewayFilterFactory(RateLimiter defaultRateLimiter,
                                                        KeyResolver defaultKeyResolver) {
        super(Config.class);
        this.defaultRateLimiter = defaultRateLimiter;
        this.defaultKeyResolver = defaultKeyResolver;
    }

    public KeyResolver getDefaultKeyResolver() {
        return defaultKeyResolver;
    }

    public RateLimiter getDefaultRateLimiter() {
        return defaultRateLimiter;
    }

    public boolean isDenyEmptyKey() {
        return denyEmptyKey;
    }

    public void setDenyEmptyKey(boolean denyEmptyKey) {
        this.denyEmptyKey = denyEmptyKey;
    }

    public String getEmptyKeyStatusCode() {
        return emptyKeyStatusCode;
    }

    public void setEmptyKeyStatusCode(String emptyKeyStatusCode) {
        this.emptyKeyStatusCode = emptyKeyStatusCode;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GatewayFilter apply(Config config) {
        KeyResolver resolver = (config.keyResolver == null) ? defaultKeyResolver : config.keyResolver;
        RateLimiter<Object> limiter = (config.rateLimiter == null) ? defaultRateLimiter : config.rateLimiter;
        return (exchange, chain) -> {
            Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            return resolver.resolve(exchange).flatMap(key ->
                    limiter.isAllowed(route.getId(), key).flatMap(response -> {
                        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                            exchange.getResponse().getHeaders().add(header.getKey(), header.getValue());
                        }
                        if (response.isAllowed()) {
                            return chain.filter(exchange);
                        }

                        ServerHttpResponse httpResponse = exchange.getResponse();
                        httpResponse.setStatusCode(HttpStatus.OK);
                        logger.error("访问已限流，请稍候再请求");
                        //RespObj respObj = RespObj.fail("访问已限流，请稍候再请求", HttpStatus.INTERNAL_SERVER_ERROR.value());
                        //DataBuffer buffer = httpResponse.bufferFactory().wrap(JSONObject.toJSONBytes(respObj));
                        String result ="访问已限流，请稍候再请求";
                        DataBuffer buffer = httpResponse.bufferFactory().wrap(JSONObject.toJSONBytes(result));
                        return httpResponse.writeWith(Mono.just(buffer));
                    }));
        };
    }

    private <T> T getOrDefault(T configValue, T defaultValue) {
        return (configValue != null) ? configValue : defaultValue;
    }

    public static class Config {

        private KeyResolver keyResolver;

        private RateLimiter rateLimiter;

        private HttpStatus statusCode = HttpStatus.TOO_MANY_REQUESTS;

        private Boolean denyEmptyKey;

        private String emptyKeyStatus;

        public KeyResolver getKeyResolver() {
            return keyResolver;
        }

        public Config setKeyResolver(KeyResolver keyResolver) {
            this.keyResolver = keyResolver;
            return this;
        }

        public RateLimiter getRateLimiter() {
            return rateLimiter;
        }

        public Config setRateLimiter(RateLimiter rateLimiter) {
            this.rateLimiter = rateLimiter;
            return this;
        }

        public HttpStatus getStatusCode() {
            return statusCode;
        }

        public Config setStatusCode(HttpStatus statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Boolean getDenyEmptyKey() {
            return denyEmptyKey;
        }

        public Config setDenyEmptyKey(Boolean denyEmptyKey) {
            this.denyEmptyKey = denyEmptyKey;
            return this;
        }

        public String getEmptyKeyStatus() {
            return emptyKeyStatus;
        }

        public Config setEmptyKeyStatus(String emptyKeyStatus) {
            this.emptyKeyStatus = emptyKeyStatus;
            return this;
        }

    }

}

