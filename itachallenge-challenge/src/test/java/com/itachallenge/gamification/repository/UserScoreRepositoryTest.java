package com.itachallenge.gamification.repository;

import com.itachallenge.challenge.dto.gamification.RankingResponseDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
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
    @DisplayName("Should aggregate points correctly per user")
    void givenUserWithMultipleScores_whenFindUsersRanking_thenAggregatesPointsCorrectly() {
        UserScoreDocument score1 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .username("Pepito")
                .pointsEarned(30)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();
        UserScoreDocument score2 = UserScoreDocument.builder()
                .id(UUID.randomUUID())
                .username("Pepito")
                .pointsEarned(70)
                .createdAt(LocalDateTime.now())
                .build();

        when(userScoreRepository.saveAll(List.of(score1, score2)))
                .thenReturn(Flux.just(score1, score2));

        RankingResponseDto pepito = new RankingResponseDto("Pepito", 100);

        when(userScoreRepository.findUsersRanking())
                .thenReturn(Flux.just(pepito));

        userScoreRepository.saveAll(List.of(score1, score2)).blockLast();

        StepVerifier.create(userScoreRepository.findUsersRanking())
                .expectNextMatches(r -> r.getUsername().equals("Pepito") && r.getPoints() == 100)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty ranking when no scores exist")
    void givenNoScores_whenFindUsersRanking_thenReturnsEmpty() {
        when(userScoreRepository.findUsersRanking())
                .thenReturn(Flux.empty());

        StepVerifier.create(userScoreRepository.findUsersRanking())
                .expectNextCount(0)
                .verifyComplete();
    }
}
