package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@DataMongoTest
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class UserScoreRepositoryTest {

    @Autowired
    private UserScoreRepository userScoreRepository;

    @BeforeEach
    void cleanDb() {
        userScoreRepository.deleteAll().block();
    }

    @Test
    void givenExistingScores_whenFindByUsername_thenReturnsSortedByDescendingDates() {
        UUID userId = UUID.randomUUID();
        UserScoreDocument score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .pointsEarned(3)
                .createdAt(LocalDateTime.now())
                .build();
        UserScoreDocument score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .pointsEarned(1)
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();
        UserScoreDocument score3 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .pointsEarned(2)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        userScoreRepository.saveAll(List.of(score1, score2, score3)).blockLast();

        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .expectNextMatches(s -> s.getPointsEarned() == 3)
                .expectNextMatches(s -> s.getPointsEarned() == 2)
                .expectNextMatches(s -> s.getPointsEarned() == 1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should aggregate points correctly per user")
    void givenUserWithMultipleScores_whenFindUsersRanking_thenAggregatesPointsCorrectly() {
        UUID validUserId = UUID.randomUUID();
        String username = "Pepito";

        UserScoreDocument score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(validUserId)
                .username(username)
                .pointsEarned(30)
                .build();
        UserScoreDocument score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(validUserId)
                .username(username)
                .pointsEarned(70)
                .build();

        userScoreRepository.saveAll(List.of(score1, score2)).blockLast();

        StepVerifier.create(userScoreRepository.findUsersRanking())
                .expectNextMatches(r -> r.getUsername().equals(username) && r.getPoints() == 100)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty ranking when no scores exist")
    void givenNoScores_whenFindUsersRanking_thenReturnsEmpty() {

        StepVerifier.create(userScoreRepository.findUsersRanking())
                .expectNextCount(0)
                .verifyComplete();
    }
}
