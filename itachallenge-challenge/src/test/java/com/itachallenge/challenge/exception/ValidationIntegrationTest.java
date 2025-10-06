package com.itachallenge.challenge.exception;

import com.itachallenge.challenge.dto.ChallengeCreateDto;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.dto.SolutionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ValidationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    // region --- Challenge Validation Tests ---

    @Test
    @DisplayName("POST /itachallenge/api/v1/challenge/solution - should fail when solution text empty")
    void addSolution_shouldFailWhenTextEmpty() {
        SolutionDto invalid = new SolutionDto(
                UUID.randomUUID(),
                "", // empty text triggers validation error
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation failed for one or more fields in solution.")
                .jsonPath("$.errors[0].message").isEqualTo("The solution text cannot be empty.");
    }

    @Test
    @DisplayName("POST /itachallenge/api/v1/challenge/challenges - should fail when fields missing")
    void addChallenge_shouldFailWhenRequiredFieldsMissing() {
        ChallengeCreateDto invalid = ChallengeCreateDto.builder()
                .challengeTitle("")
                .description("")
                .language("")
                .solution("")
                .topic(null)
                .tags(Collections.emptyList())
                .build();

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation failed for one or more fields in challengecreate.")
                .jsonPath("$.errors[?(@.field == 'challengeTitle')].message")
                .isEqualTo("The challenge title cannot be empty.");
    }

    // endregion


    // region --- Resource Validation Tests ---

    @Test
    @DisplayName("POST /itachallenge/api/v1/resource/new - should fail when required fields are null or empty")
    void createResource_shouldFailWhenFieldsInvalid() {
        ResourceDto invalid = ResourceDto.builder()
                .resourceId(null)
                .title("")
                .description("")
                .url("")
                .topic(null)
                .contentType(null)
                .challengeIds(null)
                .associationType(null)
                .build();

        webTestClient.post()
                .uri("/itachallenge/api/v1/resource/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Validation failed for one or more fields in resource.")
                .jsonPath("$.errors[?(@.field == 'resourceId')].message")
                .isEqualTo("The resource ID cannot be null.")
                .jsonPath("$.errors.length()").isEqualTo(8);
    }

    // endregion
}
