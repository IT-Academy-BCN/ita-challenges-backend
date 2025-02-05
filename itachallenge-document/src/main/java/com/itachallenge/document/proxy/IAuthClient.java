package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-auth", url = "${redirect-api.auth.url}")
public interface IAuthClient {

    @GetMapping("/api-docs")
    @CircuitBreaker(name = "itachallenge-auth", fallbackMethod = "getDefaultAuthApi")
    String getSwaggerDocs();

    default String getDefaultAuthApi(Exception exception) throws JsonProcessingException {
        return DefaultApi.getDefaultApi("auth");
    }
}
