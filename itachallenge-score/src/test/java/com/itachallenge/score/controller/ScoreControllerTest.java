package com.itachallenge.score.controller;

import com.itachallenge.score.docker.CodeExecutionManager;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ScoreControllerTest {

    @Mock
    private CodeExecutionManager codeExecutionManager;

    @InjectMocks
    private ScoreController scoreController;

    @Value("${spring.application.version}")
    private String version = "1.0.0";

    @Value("${spring.application.name}")
    private String appName = "ITAChallenge";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test GET /test endpoint")
    void testTestEndpoint() {
        String response = scoreController.test();
        assertEquals("Hello from ITA Score!!!", response);
    }

    @Test
    @DisplayName("Test GET /version endpoint")
    void testGetVersionEndpoint() {
        Mono<ResponseEntity<Map<String, String>>> responseEntityMono = scoreController.getVersion();

        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("application_name", appName);
        expectedResponse.put("version", version);

        ResponseEntity<Map<String, String>> responseEntity = responseEntityMono.block();
        assertEquals(ResponseEntity.ok(expectedResponse), responseEntity);
    }

    @Test
    @DisplayName("Test POST /score endpoint")
    void testCreateScore() {

        UUID uuidChallenge = UUID.randomUUID();
        UUID uuidLanguage = UUID.randomUUID();

        
        ScoreRequest scoreRequest = new ScoreRequest();
        scoreRequest.setSolutionText("public class Solution {}");
        scoreRequest.setUuidChallenge(uuidChallenge);
        scoreRequest.setUuidLanguage(uuidLanguage);


        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setUuidChallenge(uuidChallenge);
        scoreResponse.setUuidLanguage(uuidLanguage);
        scoreResponse.setSolutionText("public class Solution {}");
        scoreResponse.setScore(100);
        scoreResponse.setCompilationMessage("Compiled successfully");


        when(codeExecutionManager.processCode(any(ScoreRequest.class)))
                .thenReturn(ResponseEntity.ok(scoreResponse));


        Mono<ResponseEntity<ScoreResponse>> responseEntityMono = scoreController.createScore(scoreRequest);
        ResponseEntity<ScoreResponse> responseEntity = responseEntityMono.block();


        assertEquals(ResponseEntity.ok(scoreResponse), responseEntity);


        assertEquals(uuidChallenge, responseEntity.getBody().getUuidChallenge());
        assertEquals(uuidLanguage, responseEntity.getBody().getUuidLanguage());
        assertEquals("public class Solution {}", responseEntity.getBody().getSolutionText());
        assertEquals(100, responseEntity.getBody().getScore());
        assertEquals("Compiled successfully", responseEntity.getBody().getCompilationMessage());
    }
}
