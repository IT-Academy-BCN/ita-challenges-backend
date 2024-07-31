package com.itachallenge.score.controller;

import com.itachallenge.score.component.CodeExecutionService;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.filter.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
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
        when(filterChain.apply(any(String.class))).thenReturn(true);
    }

    @Test
    void createScoreSuccessful() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "int number = 1;"
        );

        webTestClient.post().uri(CONTROLLER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(scoreRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ScoreResponse.class)
                .consumeWith(response -> {
                    assert response.getResponseBody().getScore() == 99;
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

        webTestClient.post().uri(CONTROLLER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(scoreRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ScoreResponse.class)
                .consumeWith(response -> {
                    assert response.getResponseBody().getScore() == 99;
                    assert response.getResponseBody().getCompilationMessage().equals("Compilation successful");
                });
    }


    @Test
    @DisplayName("Test Score Creation with Invalid Characters")
    void createScoreWithInvalidCharacters() {
        when(filterChain.apply(any(String.class))).thenReturn(false);

        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "int number = 1; // Comment with non-ASCII character: ✓"
        );

        webTestClient.post().uri(CONTROLLER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(scoreRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ScoreResponse.class)
                .consumeWith(response -> {
                    ScoreResponse responseBody = response.getResponseBody();
                    assertEquals(0, responseBody.getScore());
                    assertEquals("The input contains a non-ASCII character", responseBody.getCompilationMessage());
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

        webTestClient.post().uri(CONTROLLER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(scoreRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ScoreResponse.class)
                .consumeWith(response -> {
                    assert response.getResponseBody().getScore() == 99;
                    assert response.getResponseBody().getCompilationMessage().equals("Compilation failed");
                });
    }

    @Test
    void testCreateScore() {
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
                .jsonPath("$.score").isEqualTo(99);

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

}

