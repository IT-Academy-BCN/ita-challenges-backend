package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.PointHistoryEntryDto;
import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.challenge.dto.gamification.ScoresHistoryResponseDto;
import com.itachallenge.challenge.dto.gamification.WeeklyPointsDto;
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
        WeeklyPointsDto entry = WeeklyPointsDto.builder()
                .period("2024-W10")
                .pointsEarned(150)
                .accumulatedAtEnd(150)
                .build();

        ScoresHistoryResponseDto responseDto = ScoresHistoryResponseDto.builder()
                .userId(validUserId)
                .totalPoints(150)
                .aggregationType("WEEKLY")
                .history(List.of(entry))
                .build();

        when(userScoreService.getUserScoresHistoryChart(validUserId)).thenReturn(Mono.just(responseDto));

        webTestClient.get()
                .uri(HISTORY_URL, validUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.user_id").isEqualTo(validUserId.toString())
                .jsonPath("$.total_points").isEqualTo(150)
                .jsonPath("$.aggregation_type").isEqualTo("WEEKLY")
                .jsonPath("$.history.length()").isEqualTo(1)
                .jsonPath("$.history[0].period").isEqualTo("2024-W10")
                .jsonPath("$.history[0].points_earned").isEqualTo(150)
                .jsonPath("$.history[0].accumulated_at_end").isEqualTo(150);
    }

    @Test
    void getUserPointsHistory_givenValidUserId_whenUserHasNoPoints_thenReturns200AndEmptyList() {
        ScoresHistoryResponseDto emptyResponse = ScoresHistoryResponseDto.builder()
                .userId(validUserId)
                .totalPoints(0)
                .aggregationType("WEEKLY")
                .history(List.of())
                .build();

        when(userScoreService.getUserScoresHistoryChart(validUserId)).thenReturn(Mono.just(emptyResponse));

        webTestClient.get()
                .uri(HISTORY_URL, validUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.total_points").isEqualTo(0)
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