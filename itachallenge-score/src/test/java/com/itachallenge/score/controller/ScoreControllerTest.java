package com.itachallenge.score.controller;

import com.itachallenge.score.component.CodeExecutionService;
import com.itachallenge.score.docker.CodeExecutionManager;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(ScoreController.class)
@ActiveProfiles("test")
class ScoreControllerTest {

    @MockBean
    private CodeExecutionManager codeExecutionManager;

    @MockBean
    private CodeExecutionService codeExecutionService;


    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private Environment env;


    private static final String CONTROLLER_URL = "/itachallenge/api/v1/score/score";

    @BeforeEach
    void setUp() {
        ExecutionResultDto mockExecutionResult = new ExecutionResultDto();
        mockExecutionResult.setMessage("Compilation successful");

        when(codeExecutionService.compileAndRunCode(any(String.class), any(String.class)))
                .thenReturn(mockExecutionResult);


        ScoreResponse mockScoreResponse = new ScoreResponse();
        mockScoreResponse.setUuidChallenge(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        mockScoreResponse.setUuidLanguage(UUID.fromString("456f7890-e89b-12d3-a456-426614174000"));
        mockScoreResponse.setSolutionText("texto de ejemplo");
        mockScoreResponse.setScore(99); // Simulando una puntuación


        when(codeExecutionManager.processCode(any(ScoreRequest.class)))
                .thenReturn(ResponseEntity.ok(mockScoreResponse));
    }




    @Test
    void getVersionTest() {
        String expectedVersion = env.getProperty("spring.application.version");

        webTestClient.get()
                .uri("/itachallenge/api/v1/score/version")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.application_name").isEqualTo("itachallenge-score")
                .jsonPath("$.version").isEqualTo("1.0.0-RELEASE");

    }


    @Test
    void testCreateScore() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                UUID.fromString("456f7890-e89b-12d3-a456-426614174000"),
                "texto de ejemplo"
        );

        webTestClient.post().uri(ScoreControllerTest.CONTROLLER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(scoreRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.uuid_challenge").isEqualTo("123e4567-e89b-12d3-a456-426614174000")
                .jsonPath("$.uuid_language").isEqualTo("456f7890-e89b-12d3-a456-426614174000")
                .jsonPath("$.solution_text").isEqualTo("Solution text example")
                .jsonPath("$.score").isEqualTo(99);

    }
}
