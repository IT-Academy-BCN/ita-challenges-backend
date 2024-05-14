package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-score", url = "${redirect-api.score.url}")
public interface IScoreClient {

    @GetMapping("/api-docs")
    @CircuitBreaker(name = "itachallenge-score", fallbackMethod = "getDefaultScoreApi")
    String getSwaggerDocs();

    default String getDefaultScoreApi(Exception exception) throws JsonProcessingException {
        return DefaultApi.getDefaultApi("score");
    }
}
