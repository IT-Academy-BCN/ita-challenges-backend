package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-score", url = "http://localhost:8763")
public interface IScoreClient {

    @GetMapping("/api-docs")
    @CircuitBreaker(name = "itachallenge-score", fallbackMethod = "getDefaultScoreApi")
    String getSwaggerDocs();

    default String getDefaultScoreApi(Exception exception) throws JsonProcessingException {
        OpenAPI openAPIDefaultScore = new OpenAPI();
        openAPIDefaultScore.setInfo(new Info()
                .title("itachallenge-Score API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-score not available."));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(openAPIDefaultScore);
    }
}
