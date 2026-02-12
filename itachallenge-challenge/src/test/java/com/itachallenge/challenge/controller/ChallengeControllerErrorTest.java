package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.MessageDto;
import com.itachallenge.challenge.exception.ChallengeNotFoundException;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.challenge.service.IChallengeJwtFacade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ChallengeControllerErrorTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IChallengeService challengeService;

    @MockBean
    private IChallengeJwtFacade challengeJwtFacade;

    @Test
    void getRelatedChallenges_NotFound_Returns404() {
        String uuid = UUID.randomUUID().toString();

        when(challengeService.getRelatedChallenges(uuid))
                .thenReturn(Mono.error(new ChallengeNotFoundException("Challenge not found")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}/related", uuid)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(MessageDto.class)
                .consumeWith(response -> assertNotNull(response.getResponseBody()));
    }

    @Test
    void deleteOneChallenge_notFound() {
        String id = "non_existing_id";

        when(challengeService.deleteChallengeById(id))
                .thenReturn(Mono.error(
                        new ChallengeNotFoundException(String.format("Challenge with id: %s not found", id))));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Challenge with id: non_existing_id not found");
    }

    @Test
    void addChallengeToBookmarks_ChallengeNotFound_Returns404() {
        String challengeId = "nonExisting_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.addChallengeToBookmarks(challengeId, userId)).thenReturn(Mono.error(new ChallengeNotFoundException(errorMessage)));
        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).addChallengeToBookmarks(challengeId, userId);
    }

    @Test
    void removeChallengeFromBookmarks_ChallengeNotFound_Returns404() {
        String challengeId = "nonExisting_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.removeChallengeFromBookmarks(challengeId, userId)).thenReturn(Mono.error(new ChallengeNotFoundException(errorMessage)));
        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/" + challengeId + "/bookmarks")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).removeChallengeFromBookmarks(challengeId, userId);
    }
}
