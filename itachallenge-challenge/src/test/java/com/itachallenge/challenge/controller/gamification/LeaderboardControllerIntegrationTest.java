package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.repository.UserScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class LeaderboardControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserScoreRepository userScoreRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.4");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        userScoreRepository.deleteAll().block();
        insertTestData();
    }

    @Test
    void getLeaderboard_returnsSortedData() {
        webTestClient.get()
                .uri("/itachallenge/api/v1/leaderboard")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.leaderboard.length()").isEqualTo(3)
                .jsonPath("$.leaderboard[0].username").isEqualTo("user3")
                .jsonPath("$.leaderboard[0].total_points").isEqualTo(50)
                .jsonPath("$.leaderboard[1].username").isEqualTo("user1")
                .jsonPath("$.leaderboard[1].total_points").isEqualTo(35)
                .jsonPath("$.leaderboard[2].username").isEqualTo("user2")
                .jsonPath("$.leaderboard[2].total_points").isEqualTo(25);
    }

    @Test
    void getLeaderboard_whenEmpty_returnsEmptyArray() {
        userScoreRepository.deleteAll().block();

        webTestClient.get()
                .uri("/itachallenge/api/v1/leaderboard")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.leaderboard").isEmpty();
    }

    private void insertTestData() {
        LocalDateTime now = LocalDateTime.now();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID userId3 = UUID.randomUUID();

        UserScoreDocument user1Score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId1)
                .username("user1")
                .challengeId(UUID.randomUUID())
                .pointsEarned(10)
                .createdAt(now.minusDays(2))
                .build();

        UserScoreDocument user1Score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId1)
                .username("user1")
                .challengeId(UUID.randomUUID())
                .pointsEarned(15)
                .createdAt(now.minusDays(1))
                .build();

        UserScoreDocument user1Score3 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId1)
                .username("user1")
                .challengeId(UUID.randomUUID())
                .pointsEarned(10)
                .createdAt(now)
                .build();

        UserScoreDocument user2Score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId2)
                .username("user2")
                .challengeId(UUID.randomUUID())
                .pointsEarned(20)
                .createdAt(now)
                .build();

        UserScoreDocument user2Score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId2)
                .username("user2")
                .challengeId(UUID.randomUUID())
                .pointsEarned(5)
                .createdAt(now.minusDays(1))
                .build();

        UserScoreDocument user3Score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId3)
                .username("user3")
                .challengeId(UUID.randomUUID())
                .pointsEarned(50)
                .createdAt(now)
                .build();

        userScoreRepository.saveAll(Flux.just(
                user1Score1, user1Score2, user1Score3,
                user2Score1, user2Score2,
                user3Score1
        )).blockLast();
    }
}
