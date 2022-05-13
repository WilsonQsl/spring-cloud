package com.sun.filter;

import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class ValidateFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        MediaType mediaType = exchange.getRequest().getHeaders().getContentType();

        // 非 application/json 请求直接放行
        if (!MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
            log.warn("非{}类型的标准后台接口调用方式，放行处理！", MediaType.APPLICATION_JSON_VALUE);
            return chain.filter(exchange);
        }

        if (request.getMethod().equals(HttpMethod.POST)) {
            if (Objects.equals(request.getHeaders().getContentLength(),0)){
                return chain.filter(exchange);
            }
            if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) || MediaType.APPLICATION_JSON_UTF8.isCompatibleWith(mediaType)) {
                System.out.println("测试数据"+exchange.getRequest().getBody().collectList());
                System.out.println(DataBufferUtils.join(exchange.getRequest().getBody()));
                //修改body，bodyHandler是我的验签、解密处理方法
                System.out.println(exchange.getRequest().getHeaders().getContentLength());
                // 检验签名
                return DataBufferUtils.join(exchange.getRequest().getBody()).map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                }).flatMap(bodyBytes -> {
                    String requestBody = new String(bodyBytes, StandardCharsets.UTF_8);
                    log.info("===>>> requestBody: \n {}", requestBody);
                    //to auth
                    //String serverSignature = checkSignature(clientId, nonce, timestamp, requestBody, signature);
                    //log.debug("===>>> serverSignature [{}]", serverSignature);

                    exchange.getAttributes().put("cacheRequestBody", requestBody);
                    return chain.filter(exchange.mutate().request(generateNewRequest(exchange.getRequest(), bodyBytes)).build());
                });
            }
        }

        // todo 优化 get 请求校验逻辑
        if (request.getMethod().equals(HttpMethod.GET)) {
            return chain.filter(exchange);
        }

        return chain.filter(exchange);
    }
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();

        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        return bodyRef.get();
    }

    private static Mono<Void> method1() {
        return Mono.defer(() -> {
            System.out.println("test");
            return Mono.empty();
        });
    }
    private Void method3(){
        System.out.println("test is null");
        return null;
    }

    private static byte[] method2(){
        System.out.println("test byte");
        String str = "hello";

        byte[] srtbyte = null;

        try {

            srtbyte = str.getBytes("UTF-8");

            String res = new String(srtbyte,"UTF-8");

            System.out.println(res);

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

        return srtbyte;
    }


    @Override
    public int getOrder() {
        return -10;
    }

    private ServerHttpRequest generateNewRequest(ServerHttpRequest request, byte[] bytes) {
        URI ex = UriComponentsBuilder.fromUri(request.getURI()).build(true).toUri();
        ServerHttpRequest newRequest = request.mutate().uri(ex).build();
        DataBuffer dataBuffer = stringBuffer(bytes);
        Flux<DataBuffer> flux = Flux.just(dataBuffer);
        newRequest = new ServerHttpRequestDecorator(newRequest) {
            @Override
            public Flux<DataBuffer> getBody() {
                return flux;
            }
        };
        return newRequest;
    }
    private DataBuffer stringBuffer(byte[] bytes) {
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        return nettyDataBufferFactory.wrap(bytes);
    }
}
