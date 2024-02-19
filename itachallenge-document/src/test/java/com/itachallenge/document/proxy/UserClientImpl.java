package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.stereotype.Component;

@Component
public class UserClientImpl implements IUserClient {

    @Override
    @CircuitBreaker(name = "itachallenge-user", fallbackMethod = "getDefaultUserApi")
    public String getSwaggerDocs() {
        // Simulate fetching Swagger documentation from an external service
        // In a real scenario, you might use Feign or some other mechanism to call an external API
        return "Actual Swagger Docs from User Service";
    }

    @Override
    public String getDefaultUserApi(Exception exception) throws JsonProcessingException {
        // Fallback method for getSwaggerDocs
        // You can customize the fallback behavior as needed
        OpenAPI openAPIDefaultUser = new OpenAPI();
        openAPIDefaultUser.setInfo(new Info()
                .title("itachallenge-User API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-user is currently unavailable!."));

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(openAPIDefaultUser);
    }
}
