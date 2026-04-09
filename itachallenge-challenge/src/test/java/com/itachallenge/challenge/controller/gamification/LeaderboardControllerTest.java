package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.LeaderboardEntryDto;
import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.gamification.service.LeaderboardService;
import com.itachallenge.gamification.service.WeeklyLeaguesResult;
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

    @Test
    void getWeeklyLeagues_returnsOkAndThreeArrays() {
        WeeklyLeaguesResult response = WeeklyLeaguesResult.builder()
                .gold(List.of(LeaderboardEntryDto.builder().username("userG1").totalPoints(100).build()))
                .silver(List.of(LeaderboardEntryDto.builder().username("userS1").totalPoints(70).build()))
                .bronze(List.of(LeaderboardEntryDto.builder().username("userB1").totalPoints(40).build()))
                .build();

        when(leaderboardService.getWeeklyLeagues()).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/itachallenge/api/v1/leaderboard/weekly")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gold.length()").isEqualTo(1)
                .jsonPath("$.silver.length()").isEqualTo(1)
                .jsonPath("$.bronze.length()").isEqualTo(1)
                .jsonPath("$.gold[0].username").isEqualTo("userG1")
                .jsonPath("$.gold[0].total_points").isEqualTo(100);

        verify(leaderboardService).getWeeklyLeagues();
    }

    @Test
    void getWeeklyLeagues_whenEmpty_returnsOkAndEmptyArrays() {
        WeeklyLeaguesResult emptyResponse = WeeklyLeaguesResult.builder()
                .gold(Collections.emptyList())
                .silver(Collections.emptyList())
                .bronze(Collections.emptyList())
                .build();

        when(leaderboardService.getWeeklyLeagues()).thenReturn(Mono.just(emptyResponse));

        webTestClient.get()
                .uri("/itachallenge/api/v1/leaderboard/weekly")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gold").isEmpty()
                .jsonPath("$.silver").isEmpty()
                .jsonPath("$.bronze").isEmpty();

        verify(leaderboardService).getWeeklyLeagues();
    }

    @Test
    void getWeeklyLeagues_whenServiceError_returns500Error() {
        when(leaderboardService.getWeeklyLeagues())
                .thenReturn(Mono.error(new InternalServerErrorException("DB error")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/leaderboard/weekly")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(leaderboardService).getWeeklyLeagues();
    }
}
