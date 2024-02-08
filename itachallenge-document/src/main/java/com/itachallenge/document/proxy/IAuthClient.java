package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-auth", url = "http://localhost:8761")
public interface IAuthClient {

    @GetMapping("/api-docs")
    @CircuitBreaker(name = "itachallenge-auth", fallbackMethod = "getDefaultAuthApi")
    String getSwaggerDocs();

    default String getDefaultAuthApi(Exception exception) throws JsonProcessingException {
        OpenAPI openAPIDefaultAuth = new OpenAPI();
        openAPIDefaultAuth.setInfo(new Info()
                .title("itachallenge-Auth API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-auth not available."));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(openAPIDefaultAuth);
    }
}
