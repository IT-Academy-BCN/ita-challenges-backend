package com.itachallenge.document.controller;

import com.itachallenge.document.config.OpenApiConfig;
import com.itachallenge.document.service.DocumentService;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@TestPropertySource(locations = "classpath:itachallenge-document/src/test/resources/application.yml")
class DocumentControllerTest {

    @Mock
    private OpenApiConfig openApiConfig;

    @Mock
    private DocumentService documentService;

    @Mock
    private Environment env;

    @InjectMocks
    private DocumentController documentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        documentController = new DocumentController(openApiConfig, documentService);
        ReflectionTestUtils.setField(documentController, "env", env);
    }

    @Test
    void shouldReturnOpenAPIForAll() {
        // Arrange
        String expectedAuthDocs = "Auth Swagger Docs";
        String expectedChallengeDocs = "Challenge Swagger Docs";
        String expectedScoreDocs = "Score Swagger Docs";
        String expectedUserDocs = "User Swagger Docs";

        when(documentService.getSwaggerAuthDocsStr()).thenReturn(expectedAuthDocs);
        when(documentService.getSwaggerChallengeDocsStr()).thenReturn(expectedChallengeDocs);
        when(documentService.getSwaggerScoreDocsStr()).thenReturn(expectedScoreDocs);
        when(documentService.getSwaggerUserDocsStr()).thenReturn(expectedUserDocs);

        OpenAPI mockOpenAPI = new OpenAPI();
        when(openApiConfig.allOpenAPI()).thenReturn(mockOpenAPI);

        // Act and Assert for "auth"
        String authResult = documentController.getSelectedOpenAPI("auth");
        assertEquals(expectedAuthDocs, authResult);

        // Act and Assert for "challenge"
        String challengeResult = documentController.getSelectedOpenAPI("challenge");
        assertEquals(expectedChallengeDocs, challengeResult);

        // Act and Assert for "score"
        String scoreResult = documentController.getSelectedOpenAPI("score");
        assertEquals(expectedScoreDocs, scoreResult);

        // Act and Assert for "user"
        String userResult = documentController.getSelectedOpenAPI("user");
        assertEquals(expectedUserDocs, userResult);

        // Act and Assert for default case
        String defaultResult = documentController.getSelectedOpenAPI("all");
        assertEquals(mockOpenAPI.toString(), defaultResult);
    }


    @Test
    void shouldReturnCorrectAppVersion() {
        // Preparar
        String expectedVersion = "1.0.0-RELEASE";
        when(env.getProperty("spring.application.version")).thenReturn(expectedVersion);

        // Actuar
        ResponseEntity<Map<String, String>> responseEntity = documentController.getVersion();

        // Afirmar
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Map<String, String> responseBody = responseEntity.getBody();
        Assertions.assertNotNull(responseBody);
        assertEquals(expectedVersion, responseBody.get("version"));
    }
}