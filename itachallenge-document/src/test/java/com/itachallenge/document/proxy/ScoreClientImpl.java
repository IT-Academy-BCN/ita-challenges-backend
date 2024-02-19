package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.stereotype.Component;

@Component
public class ScoreClientImpl implements IScoreClient {

    @Override
    @CircuitBreaker(name = "itachallenge-score", fallbackMethod = "getDefaultScoreApi")
    public String getSwaggerDocs() {
        // Simulate fetching Swagger documentation from an external service
        // In a real scenario, you might use Feign or some other mechanism to call an external API
        return "Actual Swagger Docs from Score Service";
    }

    @Override
    public String getDefaultScoreApi(Exception exception) throws JsonProcessingException {
        // Fallback method for getSwaggerDocs
        // You can customize the fallback behavior as needed
        OpenAPI openAPIDefaultScore = new OpenAPI();
        openAPIDefaultScore.setInfo(new Info()
                .title("itachallenge-Score API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-score is currently unavailable!."));

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(openAPIDefaultScore);
    }
}