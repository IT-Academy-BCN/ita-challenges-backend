package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.stereotype.Component;

@Component
public class AuthClientImpl implements IAuthClient {

    @Override
    @CircuitBreaker(name = "itachallenge-auth", fallbackMethod = "getDefaultAuthApi")
    public String getSwaggerDocs() {
        // Simulate fetching Swagger documentation from an external service
        // In a real scenario, you might use Feign or some other mechanism to call an external API
        return "Actual Swagger Docs from External Service";
    }

    @Override
    public String getDefaultAuthApi(Exception exception) throws JsonProcessingException {
        // Fallback method for getSwaggerDocs
        // You can customize the fallback behavior as needed
        OpenAPI openAPIDefaultAuth = new OpenAPI();
        openAPIDefaultAuth.setInfo(new Info()
                .title("itachallenge-Auth API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-auth is currently unavailable!."));

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(openAPIDefaultAuth);
    }
}
