package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.PointHistoryEntryDto;
import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.gamification.service.UserScoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = UserScoreController.class)
class UserScoreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserScoreService userScoreService;

    private final UUID validUserId = UUID.randomUUID();
    private static final String BASE_URL = "/itachallenge/api/v1/users/{userId}/scores";
    private static final String HISTORY_URL = BASE_URL + "/history";

    @Test
    void getUserPointsHistory_givenValidUserId_thenReturns200AndHistory() {
        PointHistoryEntryDto entry = PointHistoryEntryDto.builder()
                .createdAt("2026-03-06T11:11:11")
                .points(10)
                .build();

        PointsHistoryResponseDto responseDto = PointsHistoryResponseDto.builder()
                .username("testUser")
                .totalPoints(10)
                .history(List.of(entry))
                .build();

        when(userScoreService.getUserPointsHistory(validUserId)).thenReturn(Mono.just(responseDto));

        webTestClient.get()
                .uri(HISTORY_URL, validUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.username").isEqualTo("testUser")
                .jsonPath("$.totalPoints").isEqualTo(10)
                .jsonPath("$.history.length()").isEqualTo(1)
                .jsonPath("$.history[0].points").isEqualTo(10)
                .jsonPath("$.history[0].date").isEqualTo("2026-03-06T11:11:11");
    }

    @Test
    void getUserPointsHistory_givenValidUserId_whenUserHasNoPoints_thenReturns200AndEmptyList() {
        PointsHistoryResponseDto emptyResponse = PointsHistoryResponseDto.builder()
                .username("testUser")
                .totalPoints(0)
                .history(List.of())
                .build();

        when(userScoreService.getUserPointsHistory(validUserId)).thenReturn(Mono.just(emptyResponse));

        webTestClient.get()
                .uri(HISTORY_URL, validUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.username").isEqualTo("testUser")
                .jsonPath("$.totalPoints").isEqualTo(0)
                .jsonPath("$.history").isEmpty();
    }

    @Test
    void getUserPointsHistory_givenMalformedUUID_whenRequested_thenReturns400BadRequest() {
        String notAnId = "notValidId";

        webTestClient.get()
                .uri(HISTORY_URL, notAnId)
                .exchange()
                .expectStatus().isBadRequest();
    }
}