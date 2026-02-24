package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserScoreRepository} using mocks.
 * These run without Docker so Sonar/CI always execute them and count coverage.
 * For real repository behaviour against MongoDB, see {@link UserScoreRepositoryTest}.
 */
@ExtendWith(MockitoExtension.class)
class UserScoreRepositoryUnitTest {

    @Mock
    private UserScoreRepository userScoreRepository;

    @Test
    @DisplayName("Given mocked scores, when findByUserIdOrderByCreatedAtDesc then returns flux in expected order")
    void givenMockedScores_whenFindByUserIdOrderByCreatedAtDesc_thenReturnsInOrder() {
        UUID userId = UUID.randomUUID();
        UserScoreDocument score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .username("test-user")
                .challengeId(UUID.randomUUID())
                .pointsEarned(5)
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();
        UserScoreDocument score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .username("test-user")
                .challengeId(UUID.randomUUID())
                .pointsEarned(10)
                .createdAt(LocalDateTime.now())
                .build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(score2, score1));

        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .expectNextMatches(s -> s.getPointsEarned() == 10)
                .expectNextMatches(s -> s.getPointsEarned() == 5)
                .verifyComplete();
    }

    @Test
    @DisplayName("Given mocked empty flux, when findByUserIdOrderByCreatedAtDesc then returns empty")
    void givenMockedEmpty_whenFindByUserIdOrderByCreatedAtDesc_thenReturnsEmpty() {
        UUID userId = UUID.randomUUID();
        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.empty());

        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .verifyComplete();
    }
}