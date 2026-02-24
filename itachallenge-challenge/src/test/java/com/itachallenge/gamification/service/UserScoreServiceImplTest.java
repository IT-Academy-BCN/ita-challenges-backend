package com.itachallenge.gamification.service;

import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.repository.UserScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserScoreServiceImplTest {

    @Mock
    private UserScoreRepository userScoreRepository;

    @InjectMocks
    private UserScoreServiceImpl userScoreService;

    @Test
    void givenMultipleScores_whenGetUserPointsHistory_thenTotalPointsIsCorrectlySummed() {
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument score1 = UserScoreDocument.builder().pointsEarned(10).challengeId(UUID.randomUUID()).createdAt(now).build();
        UserScoreDocument score2 = UserScoreDocument.builder().pointsEarned(5).challengeId(UUID.randomUUID()).createdAt(now.minusDays(3)).build();
        UserScoreDocument score3 = UserScoreDocument.builder().pointsEarned(20).challengeId(UUID.randomUUID()).createdAt(now.minusDays(1)).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Flux.just(score1, score2, score3));

        var result = userScoreService.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getTotalPoints() == 35)
                .verifyComplete();
    }

    @Test
    void givenMultipleScores_whenGetUserPointsHistory_thenHistoryListIsCorrectlyMapped() {
        UUID userId = UUID.randomUUID();
        LocalDateTime fixedDate = LocalDateTime.of(2015, 3, 26, 7, 33, 21);
        String expectedDate = fixedDate.toString();

        UserScoreDocument score = UserScoreDocument.builder().pointsEarned(10).challengeId(UUID.randomUUID()).createdAt(fixedDate).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(score));

        var result = userScoreService.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getHistory().size() == 1 &&
                                response.getHistory().getFirst().getCreatedAt().equals(expectedDate))
                .verifyComplete();
    }

    @Test
    void givenNoScores_whenGetUserPointsHistory_thenReturnsEmptyHistoryAndZeroPoints() {
        UUID userId = UUID.randomUUID();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Flux.empty());

        var result = userScoreService.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getTotalPoints() == 0 &&
                                response.getHistory().isEmpty())
                .verifyComplete();
    }

    @Test
    void givenScoresWithNullPoints_whenGetUserPointsHistory_thenTotalPointsIgnoresNullValues() {
        UUID userId = UUID.randomUUID();
        UserScoreDocument validScore = UserScoreDocument.builder().challengeId(UUID.randomUUID()).pointsEarned(10).build();
        UserScoreDocument invalidScore = UserScoreDocument.builder().challengeId(UUID.randomUUID()).pointsEarned(null).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(validScore, invalidScore));

        var result = userScoreService.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTotalPoints() == 10)
                .verifyComplete();
    }

    @Test
    void givenValidScores_whenGetUserPointsHistory_thenHistoryIsReturnedInCorrectOrder() {
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument latestScore = UserScoreDocument.builder().pointsEarned(10).challengeId(UUID.randomUUID()).createdAt(now).build();
        UserScoreDocument olderScore = UserScoreDocument.builder().pointsEarned(5).challengeId(UUID.randomUUID()).createdAt(now.minusDays(3)).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(latestScore, olderScore));

        var result = userScoreService.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getHistory().size() == 2 &&
                                response.getHistory().getFirst().getPoints() == 10 &&
                                response.getHistory().get(1).getPoints() == 5)
                .verifyComplete();
    }
}