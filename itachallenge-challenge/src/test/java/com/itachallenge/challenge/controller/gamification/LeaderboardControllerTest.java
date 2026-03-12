package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import com.itachallenge.gamification.service.LeaderboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.Mockito.when;

@WebFluxTest(LeaderboardController.class)
class LeaderboardControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LeaderboardService leaderboardService;

    @Test
    void getLeaderboard_returnsOk() {
        LeaderboardResponseDto responseDto = LeaderboardResponseDto.builder()
                .leaderboard(Collections.emptyList())
                .build();

        when(leaderboardService.getLeaderboard()).thenReturn(Mono.just(responseDto));

        webTestClient.get()
                .uri("/itachallenge/api/v1/users/leaderboard")
                .exchange()
                .expectStatus().isOk()
                .expectBody(LeaderboardResponseDto.class);
    }
}
