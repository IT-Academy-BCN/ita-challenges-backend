package com.itachallenge.score.controller;

import com.itachallenge.score.component.CodeExecutionService;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.filter.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(ScoreController.class)
@ActiveProfiles("test")
class ScoreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private Filter filterChain;

    @Autowired
    private Environment env;

    @MockBean
    private CodeExecutionService codeExecutionService;

    private static final String CONTROLLER_URL = "/itachallenge/api/v1/score/score";

    @BeforeEach
    void setUp() {
        ExecutionResultDto mockExecutionResult = new ExecutionResultDto();
        mockExecutionResult.setMessage("Compilation successful");
        when(codeExecutionService.compileAndRunCode(any(String.class), any(String.class)))
                .thenReturn(mockExecutionResult);
        when(filterChain.apply(any(String.class), any())).thenReturn(mockExecutionResult);
    }

    @Test
    void createScoreSuccessful() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "int number = 1;"
        );

        int score = codeExecutionService.calculateScore(new ExecutionResultDto());
        when(codeExecutionService.calculateScore(any(ExecutionResultDto.class))).thenReturn(score);
        webTestClient.post().uri(CONTROLLER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(scoreRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ScoreResponse.class)
                .consumeWith(response -> {
                    assert response.getResponseBody().getScore() == score;
                    assert response.getResponseBody().getCompilationMessage().equals("Compilation successful");
                });
    }

    @Test
    void createScoreWithEmptySourceCode() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ""
        );
        int score = codeExecutionService.calculateScore(new ExecutionResultDto());
        webTestClient.post().uri(CONTROLLER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(scoreRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ScoreResponse.class)
                .consumeWith(response -> {
                    assert response.getResponseBody().getScore() == score;
                    assert response.getResponseBody().getCompilationMessage().equals("Compilation successful");
                });
    }



    @Test
    void createScoreCompilationFailure() {
        ExecutionResultDto mockExecutionResult = new ExecutionResultDto();
        mockExecutionResult.setMessage("Compilation failed");
        when(codeExecutionService.compileAndRunCode(any(String.class), any(String.class)))
                .thenReturn(mockExecutionResult);

        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "int number = 1 / 0;" // Code that will cause compilation/runtime error
        );
        int score = codeExecutionService.calculateScore(new ExecutionResultDto());
        webTestClient.post().uri(CONTROLLER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(scoreRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ScoreResponse.class)
                .consumeWith(response -> {
                    assert response.getResponseBody().getScore() == score;
                    assert response.getResponseBody().getCompilationMessage().equals("Compilation failed");
                });
    }

    @Test
    void testCreateScore() {
        int score = codeExecutionService.calculateScore(new ExecutionResultDto());
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                UUID.fromString("456f7890-e89b-12d3-a456-426614174000"),
                "texto de ejemplo"
        );

        webTestClient.post().uri(CONTROLLER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(scoreRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.uuid_challenge").isEqualTo("123e4567-e89b-12d3-a456-426614174000")
                .jsonPath("$.uuid_language").isEqualTo("456f7890-e89b-12d3-a456-426614174000")
                .jsonPath("$.solution_text").isEqualTo("texto de ejemplo")
                .jsonPath("$.score").isEqualTo(score);
    }

    @Test
    void getVersionTest() {
        webTestClient.get()
                .uri("/itachallenge/api/v1/score/version")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.application_name").isEqualTo("itachallenge-score")
                .jsonPath("$.version").isEqualTo("1.0.0-RELEASE");
    }
}
