package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.LeaderboardEntryDto;
import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.gamification.service.LeaderboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(LeaderboardController.class)
class LeaderboardControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LeaderboardService leaderboardService;

    @Test
    void getLeaderboard_returnsOkAndLeaderboard() {
        LeaderboardResponseDto expectedResponse = LeaderboardResponseDto.builder()
                .leaderboard(List.of(
                        LeaderboardEntryDto.builder().username("user2").totalPoints(50).build(),
                        LeaderboardEntryDto.builder().username("user1").totalPoints(35).build()
                ))
                .build();

        when(leaderboardService.getLeaderboard()).thenReturn(Mono.just(expectedResponse));

        webTestClient.get()
                .uri("/itachallenge/api/v1/leaderboard")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.leaderboard.length()").isEqualTo(2)
                .jsonPath("$.leaderboard[0].username").isEqualTo("user2")
                .jsonPath("$.leaderboard[0].total_points").isEqualTo(50)
                .jsonPath("$.leaderboard[1].username").isEqualTo("user1")
                .jsonPath("$.leaderboard[1].total_points").isEqualTo(35);

        verify(leaderboardService).getLeaderboard();
    }


    @Test
    void getLeaderboard_whenEmpty_returnsOkAndEmptyArray() {
        LeaderboardResponseDto emptyResponse = LeaderboardResponseDto.builder()
                .leaderboard(Collections.emptyList())
                .build();

        when(leaderboardService.getLeaderboard()).thenReturn(Mono.just(emptyResponse));

        webTestClient.get()
                .uri("/itachallenge/api/v1/leaderboard")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.leaderboard").isEmpty();

        verify(leaderboardService).getLeaderboard();
    }

    @Test
    void getLeaderboard_whenServiceError_returns500Error() {
        when(leaderboardService.getLeaderboard())
                .thenReturn(Mono.error(new InternalServerErrorException("DB error")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/leaderboard")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(leaderboardService).getLeaderboard();
    }
}
