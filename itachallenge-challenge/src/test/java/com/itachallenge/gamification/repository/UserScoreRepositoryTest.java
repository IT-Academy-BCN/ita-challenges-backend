package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
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

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(score1, score3, score2));

        StepVerifier.create(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .expectNextMatches(s -> s.getPointsEarned() == 3)
                .expectNextMatches(s -> s.getPointsEarned() == 2)
                .expectNextMatches(s -> s.getPointsEarned() == 1)
                .verifyComplete();
    }

    @Test
    void givenUserScores_whenAggregateUserScores_thenReturnsCorrectAggregation() {
        UserScoreAggregation mockAgg1 = new UserScoreAggregation() {
            @Override public UUID get_id() { return UUID.randomUUID(); }
            @Override public String getUsername() { return "user1"; }
            @Override public int getTotalPoints() { return 150; }
        };

        UserScoreAggregation mockAgg2 = new UserScoreAggregation() {
            @Override public UUID get_id() { return UUID.randomUUID(); }
            @Override public String getUsername() { return "user2"; }
            @Override public int getTotalPoints() { return 300; }
        };

        when(userScoreRepository.aggregateUserScores(anyInt(), anyInt()))
                .thenReturn(Flux.just(mockAgg1, mockAgg2));

        Flux<UserScoreAggregation> result = userScoreRepository.aggregateUserScores(0, 10);

        StepVerifier.create(result)
                .expectNextMatches(agg -> agg.getUsername().equals("user1") && agg.getTotalPoints() == 150)
                .expectNextMatches(agg -> agg.getUsername().equals("user2") && agg.getTotalPoints() == 300)
                .verifyComplete();
    }

    @Test
    void givenUserScoresAndPagination_whenAggregateUserScores_thenRespectsSkipAndLimit() {
        UserScoreAggregation mockAgg = new UserScoreAggregation() {
            @Override public UUID get_id() { return UUID.randomUUID(); }
            @Override public String getUsername() { return "testUser"; }
            @Override public int getTotalPoints() { return 100; }
        };

        when(userScoreRepository.aggregateUserScores(2, 1))
                .thenReturn(Flux.just(mockAgg));

        Flux<UserScoreAggregation> result = userScoreRepository.aggregateUserScores(2, 1);

        StepVerifier.create(result)
                .expectNextMatches(agg -> agg.getUsername().equals("testUser") && agg.getTotalPoints() == 100)
                .verifyComplete();
    }

    @Test
    void givenNoUsers_whenCountDistinctUsers_thenReturnsZero() {
        when(userScoreRepository.countDistinctUsers())
                .thenReturn(Mono.just(0L));

        Mono<Long> count = userScoreRepository.countDistinctUsers();

        StepVerifier.create(count)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void givenMultipleUsers_whenCountDistinctUsers_thenReturnsCorrectCount() {
        when(userScoreRepository.countDistinctUsers())
                .thenReturn(Mono.just(7L));

        Mono<Long> count = userScoreRepository.countDistinctUsers();

        StepVerifier.create(count)
                .expectNext(7L)
                .verifyComplete();
    }

    @Test
    void givenEmptyAggregation_whenAggregateUserScores_thenReturnsEmpty() {
        when(userScoreRepository.aggregateUserScores(anyInt(), anyInt()))
                .thenReturn(Flux.empty());

        Flux<UserScoreAggregation> result = userScoreRepository.aggregateUserScores(0, 10);

        StepVerifier.create(result)
                .verifyComplete();
    }
}
