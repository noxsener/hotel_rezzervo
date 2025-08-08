package com.interestingdomain.gateway.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
@Slf4j
public class LoggingGlobalFiltersConfigurations {

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                }));
    }

    @Bean
    public ErrorAttributes errorAttributes() {
//        return new CustomErrorAttributes(httpStatusExceptionTypeMapper);
        return new CustomErrorAttributes();
    }

    public class CustomErrorAttributes extends DefaultErrorAttributes {
        @Override
        public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
            Map<String, Object> attributes = super.getErrorAttributes(request, options);
            Throwable error = getError(request);
            MergedAnnotation<ResponseStatus> responseStatusAnnotation = MergedAnnotations
                    .from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class);
            HttpStatus errorStatus = determineHttpStatus(error, responseStatusAnnotation);
            attributes.put("status", errorStatus.value());
            return attributes;
        }

        private HttpStatus determineHttpStatus(Throwable error, MergedAnnotation<ResponseStatus> responseStatusAnnotation) {
            if (error instanceof ResponseStatusException) {
                return HttpStatus.resolve(((ResponseStatusException) error).getStatusCode().value());
            }
            return responseStatusAnnotation.getValue("code", HttpStatus.class).orElseGet(() -> {
                if (error instanceof java.net.ConnectException) {
                    return HttpStatus.SERVICE_UNAVAILABLE;
                }
                return HttpStatus.INTERNAL_SERVER_ERROR;
            });
        }
    }
}
