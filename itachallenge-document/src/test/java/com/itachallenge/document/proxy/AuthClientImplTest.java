package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthClientImplTest {

    private AuthClientImpl authClient;

    @BeforeEach
    void setUp() {
        authClient = new AuthClientImpl(); // Instanciate your implementation class
    }

    @Test
    void testGetSwaggerDocs() {
        /// Simulate fetching Swagger documentation from an external service
        String swaggerDocs = authClient.getSwaggerDocs();

        // Assert that the returned value is not null or empty
        assertNotNull(swaggerDocs);
        Assertions.assertFalse(swaggerDocs.isEmpty());
    }

    @Test
    void testGetDefaultAuthApi() throws JsonProcessingException {
        // Implement the behavior of your getDefaultAuthApi method
        String defaultAuthApi = authClient.getDefaultAuthApi(new RuntimeException("Simulate FeignClient failure"));

        // Implement assertions based on the expected behavior of getDefaultAuthApi
        // For example, you can check if the returned string matches your expected JSON structure
        ObjectMapper objectMapper = new ObjectMapper();
        OpenAPI expectedOpenAPI = new OpenAPI();
        expectedOpenAPI.setInfo(new Info()
                .title("itachallenge-Auth API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-auth is currently unavailable!."));
        String expectedJson = objectMapper.writeValueAsString(expectedOpenAPI);

        assertEquals(expectedJson, defaultAuthApi);
    }
}