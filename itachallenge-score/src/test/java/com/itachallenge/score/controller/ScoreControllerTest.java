package com.itachallenge.score.controller;

import com.itachallenge.score.document.ScoreRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ScoreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private Environment env;

    private static final String CONTROLLER_URL = "/itachallenge/api/v1/score/score";

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

