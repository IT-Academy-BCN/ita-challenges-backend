package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-challenge", url = "http://localhost:8762")
public interface IChallengeClient {

    @GetMapping("/api-docs")
    @CircuitBreaker(name = "itachallenge-challenge", fallbackMethod = "getDefaultChallengeApi")
    String getSwaggerDocs();

    default String getDefaultChallengeApi(Exception exception) throws JsonProcessingException {
        OpenAPI openAPIDefaultChallenge = new OpenAPI();
        openAPIDefaultChallenge.setInfo(new Info()
                .title("itachallenge-Challenge API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-challenge not available."));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(openAPIDefaultChallenge);
    }
}
