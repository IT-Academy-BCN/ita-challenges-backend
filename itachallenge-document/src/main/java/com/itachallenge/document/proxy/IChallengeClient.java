package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-challenge", url = "http://localhost:8762")
public interface IChallengeClient {

    @GetMapping("/api-docs")
    @CircuitBreaker(name = "itachallenge-challenge", fallbackMethod = "getDefaultChallengeApi")
    String getSwaggerDocs();

    default String getDefaultChallengeApi(Exception exception) throws JsonProcessingException {
        return DefaultApi.getDefaultApi("challenge");
    }
}
