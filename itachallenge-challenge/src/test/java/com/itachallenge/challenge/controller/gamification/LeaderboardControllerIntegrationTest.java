package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import com.itachallenge.gamification.repository.UserScoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class LeaderboardControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserScoreRepository userScoreRepository;

    @Test
    void getLeaderboard_integrationTest() {
        userScoreRepository.deleteAll().block();

        webTestClient.get()
                .uri("/itachallenge/api/v1/users/leaderboard")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.leaderboard").isArray();
    }
}
