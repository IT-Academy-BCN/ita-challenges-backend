package com.itachallenge.score.repository.controller;

import com.itachallenge.score.controller.ScoreController;
import com.itachallenge.score.document.ScoreRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.UUID;

@WebFluxTest(ScoreController.class)
 class IScoreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCreateScore() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                UUID.fromString("456f7890-e89b-12d3-a456-426614174000"),
                "texto de ejemplo"
        );

        webTestClient.post().uri("/itachallenge/api/v1/score/score")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(scoreRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.uuidChallenge").isEqualTo("123e4567-e89b-12d3-a456-426614174000")
                .jsonPath("$.uuidLanguage").isEqualTo("456f7890-e89b-12d3-a456-426614174000")
                .jsonPath("$.solutionText").isEqualTo("texto de ejemplo")
                .jsonPath("$.score").isNumber();
    }
}
