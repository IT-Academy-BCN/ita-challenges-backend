package com.itachallenge.document.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserClientImplTest {

    private IUserClient userClient;

    @BeforeEach
    void setUp() {
        userClient = new UserClientImpl(); // Instantiate your implementation class
    }

    @Test
    void testGetSwaggerDocs() {
        // Simulate fetching Swagger documentation from the user service
        String swaggerDocs = userClient.getSwaggerDocs();

        // Assert that the returned value is not null and not empty
        assertNotNull(swaggerDocs);
        assertFalse(swaggerDocs.isEmpty());
    }

    @Test
    void testGetDefaultUserApi() throws JsonProcessingException {
        // Implement the behavior of your getDefaultUserApi method
        String defaultUserApi = userClient.getDefaultUserApi(new RuntimeException("Simulate FeignClient failure"));

        // Implement assertions based on the expected behavior of getDefaultUserApi
        // For example, you can check if the returned string matches your expected JSON structure
        ObjectMapper objectMapper = new ObjectMapper();
        OpenAPI expectedOpenAPI = new OpenAPI();
        expectedOpenAPI.setInfo(new Info()
                .title("itachallenge-User API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-user is currently unavailable!."));
        String expectedJson = objectMapper.writeValueAsString(expectedOpenAPI);

        assertEquals(expectedJson, defaultUserApi);
    }
}
