package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "itachallenge-user", url = "http://localhost:8764")
public interface IUserClient {

    @GetMapping("/api-docs")
    @CircuitBreaker(name = "itachallenge-user", fallbackMethod = "getDefaultUserApi")
    String getSwaggerDocs();

    default String getDefaultUserApi(Exception exception) throws JsonProcessingException {
        OpenAPI openAPIDefaultUser = new OpenAPI();
        openAPIDefaultUser.setInfo(new Info()
                .title("itachallenge-User API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-user not available."));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(openAPIDefaultUser);
    }
}