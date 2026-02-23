package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserScoreRepositoryTest {

    @Mock
    private UserScoreRepository userScoreRepository;

    @Test
    void givenExistingScores_whenFindByUsername_thenReturnsSortedByDescendingDates() {
        UUID userId = UUID.randomUUID();

        UserScoreDocument score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .points(3)
                .createdAt(LocalDateTime.now())
                .build();
        UserScoreDocument score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .points(1)
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();
        UserScoreDocument score3 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .points(2)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(score1, score3, score2));

        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .expectNextMatches(s -> s.getPoints() == 3)
                .expectNextMatches(s -> s.getPoints() == 2)
                .expectNextMatches(s -> s.getPoints() == 1)
                .verifyComplete();
    }
}
