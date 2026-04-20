package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.enums.ActivityType;
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

    private final UUID userId = UUID.randomUUID();

    @Test
    void givenExistingScores_whenFindByUsername_thenReturnsSortedByDescendingDates() {
        UserScoreDocument score1 = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .id(UUID.randomUUID())
                .userId(userId)
                .pointsEarned(3)
                .createdAt(LocalDateTime.now())
                .build();
        UserScoreDocument score2 = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .id(UUID.randomUUID())
                .userId(userId)
                .pointsEarned(1)
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();
        UserScoreDocument score3 = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .id(UUID.randomUUID())
                .userId(userId)
                .pointsEarned(2)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(score1, score3, score2));

        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .expectNextMatches(s -> s.getPointsEarned() == 3)
                .expectNextMatches(s -> s.getPointsEarned() == 2)
                .expectNextMatches(s -> s.getPointsEarned() == 1)
                .verifyComplete();
    }

    @Test
    void givenExistingScores_whenFindByUserIdOrderByCreatedAtAsc_thenReturnsSortedByAscendingDates() {
        UserScoreDocument score1 = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .id(UUID.randomUUID()).userId(userId)
                .pointsEarned(3).createdAt(LocalDateTime.now()).build();
        UserScoreDocument score2 = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .id(UUID.randomUUID()).userId(userId)
                .pointsEarned(1).createdAt(LocalDateTime.now().minusDays(3)).build();
        UserScoreDocument score3 = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .id(UUID.randomUUID()).userId(userId)
                .pointsEarned(2).createdAt(LocalDateTime.now().minusDays(1)).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(score2, score3, score1));

        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .expectNextMatches(s -> s.getPointsEarned() == 1)
                .expectNextMatches(s -> s.getPointsEarned() == 2)
                .expectNextMatches(s -> s.getPointsEarned() == 3)
                .verifyComplete();
    }
}
