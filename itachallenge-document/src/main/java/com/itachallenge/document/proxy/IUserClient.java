package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-user", url = "${redirect-api.user.url}")
public interface IUserClient {

    @GetMapping("/api-docs")
    @CircuitBreaker(name = "itachallenge-user", fallbackMethod = "getDefaultUserApi")
    String getSwaggerDocs();

    default String getDefaultUserApi(Exception exception) throws JsonProcessingException {
        return DefaultApi.getDefaultApi("user");
    }
}