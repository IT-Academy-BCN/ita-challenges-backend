package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.stereotype.Component;

@Component
public class ChallengeClientImpl implements IChallengeClient {

    @Override
    @CircuitBreaker(name = "itachallenge-challenge", fallbackMethod = "getDefaultChallengeApi")
    public String getSwaggerDocs() {
        // Simulate fetching Swagger documentation from an external service
        // In a real scenario, you might use Feign or some other mechanism to call an external API
        return "Actual Swagger Docs from Challenge Service";
    }

    @Override
    public String getDefaultChallengeApi(Exception exception) throws JsonProcessingException {
        // Fallback method for getSwaggerDocs
        // You can customize the fallback behavior as needed
        OpenAPI openAPIDefaultChallenge = new OpenAPI();
        openAPIDefaultChallenge.setInfo(new Info()
                .title("itachallenge-Challenge API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-challenge is currently unavailable!."));

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(openAPIDefaultChallenge);
    }
}
