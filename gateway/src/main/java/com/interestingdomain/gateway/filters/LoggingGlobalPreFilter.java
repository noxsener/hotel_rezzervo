package com.interestingdomain.gateway.filters;

import io.netty.handler.codec.http.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingGlobalPreFilter implements GlobalFilter {


    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {
        try {
            ServerHttpRequest request = exchange.getRequest();

            org.springframework.http.HttpMethod method = request.getMethod();
            URI uri = request.getURI();
            HttpHeaders headers = request.getHeaders();
            String path = request.getPath().value();

            if (HttpMethod.OPTIONS.name().equals(request.getMethod().name())) {
                return chain.filter(exchange);
            }
            List<String> headerList = request.getHeaders().getOrEmpty("Authorization");
            String header = !CollectionUtils.isEmpty(headerList) ? CollectionUtils.firstElement(headerList) : null;
            if ((header == null || !validateJwt(header.substring(header.indexOf(' ') + 1)))
            ) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private String doHMACSHA256(String part1AndPart2)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec("7208ad6a531647939f610a2005649aa5".getBytes(), "HS256"));
        byte[] hashBytes = mac.doFinal(part1AndPart2.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    public boolean validateJwt(String jwt) {
        try {
            if (StringUtils.isBlank(jwt)) {
                return Boolean.FALSE;
            }
            String[] parts = jwt.split("\\.");
            String part3 = parts[2];
            String part1_part2 = parts[0] + "." + parts[1];
            String jwtSignature = Base64.getEncoder().encodeToString(
                    (doHMACSHA256(part1_part2)).getBytes(StandardCharsets.UTF_8));
            return jwtSignature.equals(part3);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            return Boolean.FALSE;
        }
    }

}
