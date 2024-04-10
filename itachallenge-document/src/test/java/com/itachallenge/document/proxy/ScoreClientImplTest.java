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

public class ScoreClientImplTest {

    private IScoreClient scoreClient;

    @BeforeEach
    void setUp() {
        scoreClient = new ScoreClientImpl(); // Instantiate your implementation class
    }

    @Test
    void testGetSwaggerDocs() {
        // Simulate fetching Swagger documentation from the score service
        String swaggerDocs = scoreClient.getSwaggerDocs();

        // Assert that the returned value is not null and not empty
        assertNotNull(swaggerDocs);
        assertFalse(swaggerDocs.isEmpty());
    }

    @Test
    void testGetDefaultScoreApi() throws JsonProcessingException {
        // Implement the behavior of your getDefaultScoreApi method
        String defaultScoreApi = scoreClient.getDefaultScoreApi(new RuntimeException("Simulate FeignClient failure"));

        // Implement assertions based on the expected behavior of getDefaultScoreApi
        // For example, you can check if the returned string matches your expected JSON structure
        ObjectMapper objectMapper = new ObjectMapper();
        OpenAPI expectedOpenAPI = new OpenAPI();
        expectedOpenAPI.setInfo(new Info()
                .title("itachallenge-Score API Documentation")
                .version("1.0")
                .description("API documentation for itachallenge-score is currently unavailable!."));
        String expectedJson = objectMapper.writeValueAsString(expectedOpenAPI);

        assertEquals(expectedJson, defaultScoreApi);
    }
}
