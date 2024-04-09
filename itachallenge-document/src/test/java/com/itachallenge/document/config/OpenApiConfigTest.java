package com.itachallenge.document.config;

import com.itachallenge.document.service.DocumentService;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class OpenApiConfigTest {

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private OpenApiConfig openApiConfig;

    @Test
    void shouldCreateAllOpenAPI() {
        // Mock Swagger docs from DocumentService
        when(documentService.getSwaggerChallengeDocsStr()).thenReturn(
                """
                {
                    "openapi": "3.0.0",
                    "info": {
                        "title": "Challenge API",
                        "version": "1.0"
                    }
                }"""
        );

        when(documentService.getSwaggerUserDocsStr()).thenReturn(
                """
                {
                    "openapi": "3.0.0",
                    "info": {
                        "title": "User API",
                        "version": "1.0"
                    }
                }"""
        );

        when(documentService.getSwaggerScoreDocsStr()).thenReturn(
                """
                {
                    "openapi": "3.0.0",
                    "info": {
                        "title": "Score API",
                        "version": "1.0"
                    }
                }"""
        );

        when(documentService.getSwaggerAuthDocsStr()).thenReturn(
                """
                {
                    "openapi": "3.0.0",
                    "info": {
                        "title": "Auth API",
                        "version": "1.0"
                    }
                }"""
        );

        // Perform test
        OpenAPI result = openApiConfig.allOpenAPI();

        // Assertions
        assertThat(result).isNotNull();
        assertThat(result.getInfo().getTitle()).isEqualTo("ITA Challenges APIs Documentation");
        assertThat(result.getInfo().getVersion()).isEqualTo("1.0");
        assertThat(result.getInfo().getDescription()).isEqualTo("Centralized documentation for ITA Challenges APIs. Explore and understand the available services for authentication, challenges, user management, scoring, and more.");

        // Verify extensions
        assertThat(result.getExtensions()).containsKey("itachallenge-challenge-api");
        assertThat(result.getExtensions()).containsKey("itachallenge-user-api");
        assertThat(result.getExtensions()).containsKey("itachallenge-score-api");
        assertThat(result.getExtensions()).containsKey("itachallenge-auth-api");
    }
}