package com.itachallenge.document.controller;

import com.itachallenge.document.config.OpenApiConfig;
import com.itachallenge.document.service.DocumentService;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class DocumentControllerTest {

    @Mock
    private OpenApiConfig openApiConfig;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
}
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.itachallenge.document.config.OpenApiConfig;
//import com.itachallenge.document.service.DocumentService;
//import com.itachallenge.document.proxy.IAuthClient;
//import com.itachallenge.document.proxy.IChallengeClient;
//import com.itachallenge.document.proxy.IScoreClient;
//import com.itachallenge.document.proxy.IUserClient;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(DocumentController.class)
//class DocumentControllerTest {
//
//    @Autowired
//    private  MockMvc mockMvc;
//
//    @MockBean
//    private OpenApiConfig openApiConfig;
//
//    @MockBean
//    private DocumentService documentService;
//
//    @InjectMocks
//    private DocumentController documentController;
//
//    @Mock
//    private IChallengeClient challengeClient;
//
//    @Mock
//    private IUserClient userClient;
//
//    @Mock
//    private IAuthClient authClient;
//
//    @Mock
//    private IScoreClient scoreClient;
//
//    @Autowired
//    private  ObjectMapper objectMapper;
//
//    @Test
//    void shouldReturnSwaggerAuthDocs() throws Exception {
//        String expectedDocs = "Auth Swagger Docs";
//        when(documentService.getSwaggerAuthDocsStr()).thenReturn(expectedDocs);
//
//        mockMvc.perform(get("/api-docs/auth"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(expectedDocs));
//    }
//
//    @Test
//    void shouldReturnSwaggerChallengeDocs() throws Exception {
//        String expectedDocs = "Challenge Swagger Docs";
//        when(documentService.getSwaggerChallengeDocsStr()).thenReturn(expectedDocs);
//
//        mockMvc.perform(get("/api-docs/challenge"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(expectedDocs));
//    }
//
//    @Test
//    void shouldReturnSwaggerScoreDocs() throws Exception {
//        String expectedDocs = "Score Swagger Docs";
//        when(documentService.getSwaggerScoreDocsStr()).thenReturn(expectedDocs);
//
//        mockMvc.perform(get("/api-docs/score"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(expectedDocs));
//    }
//
//    @Test
//    void shouldReturnSwaggerUserDocs() throws Exception {
//        String expectedDocs = "User Swagger Docs";
//        when(documentService.getSwaggerUserDocsStr()).thenReturn(expectedDocs);
//
//        mockMvc.perform(get("/api-docs/user"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(expectedDocs));
//    }
//
//    @Test
//    void shouldReturnAllOpenAPI() throws Exception {
//        String expectedDocs = "All OpenAPI Docs";
//        when(openApiConfig.allOpenAPI().toString()).thenReturn(expectedDocs);
//
//        mockMvc.perform(get("/api-docs"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(expectedDocs));
//    }
//}
