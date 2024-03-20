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

public class ChallengeClientImplTest {

    private IChallengeClient challengeClient;

    @BeforeEach
    void setUp() {
        challengeClient = new ChallengeClientImpl(); // Instantiate your implementation class
    }

    @Test
    void testGetSwaggerDocs() {
        // Simulate fetching Swagger documentation from the challenge service
        String swaggerDocs = challengeClient.getSwaggerDocs();

        // Assert that the returned value is not null and not empty
        assertNotNull(swaggerDocs);
        assertFalse(swaggerDocs.isEmpty());
    }

    @Test
    void testGetDefaultChallengeApi() throws JsonProcessingException {
        // Implement the behavior of your getDefaultChallengeApi method
        String defaultChallengeApi = challengeClient.getDefaultChallengeApi(new RuntimeException("Simulate FeignClient failure"));

        // Implement assertions based on the expected behavior of getDefaultChallengeApi
        // For example, you can check if the returned string matches your expected JSON structure
        ObjectMapper objectMapper = new ObjectMapper();
        OpenAPI expectedOpenAPI = new OpenAPI();
        expectedOpenAPI.setInfo(new Info()
                .title("itachallenge-Challenge API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-challenge is currently unavailable!."));
        String expectedJson = objectMapper.writeValueAsString(expectedOpenAPI);

        assertEquals(expectedJson, defaultChallengeApi);
    }
}
