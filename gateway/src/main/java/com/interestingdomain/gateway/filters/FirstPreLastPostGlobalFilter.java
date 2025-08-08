package com.interestingdomain.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class FirstPreLastPostGlobalFilter
        implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        try {

            ServerHttpRequest request = exchange.getRequest();

            HttpMethod method = request.getMethod();
            URI uri = request.getURI();
            HttpHeaders headers = request.getHeaders();
            String path = request.getPath().value();

//            log.info("Uri: {}, path: {}, Method: {}, headers: {}", uri, path, method, headers);

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {

                    }));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
