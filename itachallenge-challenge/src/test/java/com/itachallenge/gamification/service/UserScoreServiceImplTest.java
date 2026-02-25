package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.RankingResponseDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.repository.UserScoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    void givenDescendingRepoData_whenGetUserPointsHistory_thenReturnsAscendingForFrontend() {
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument latest = UserScoreDocument.builder()
                .pointsEarned(10)
                .createdAt(now)
                .build();
        UserScoreDocument older = UserScoreDocument.builder()
                .pointsEarned(5)
                .createdAt(now.minusDays(3))
                .build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Flux.just(latest, older));

        var result = userScoreService.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getHistory().size() == 2 &&
                        response.getHistory().getFirst().getPoints() == 5 &&
                        response.getHistory().get(1).getPoints() == 10)
                .verifyComplete();
    }

    @Test
    void givenScoreDocument_whenGetUserPointsHistory_thenDateIsCorrectlyMapped() {
        UUID userId = UUID.randomUUID();
        LocalDateTime fixedDate = LocalDateTime.of(2015, 3, 26, 7, 33, 21);
        String expectedDate = fixedDate.toString();

        UserScoreDocument score = UserScoreDocument.builder()
                .pointsEarned(10)
                .challengeId(UUID.randomUUID())
                .createdAt(fixedDate)
                .build();

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
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument validScore = UserScoreDocument.builder()
                .challengeId(UUID.randomUUID())
                .pointsEarned(10)
                .createdAt(now)
                .build();
        UserScoreDocument invalidScore = UserScoreDocument.builder()
                .challengeId(UUID.randomUUID())
                .pointsEarned(null)
                .createdAt(now.minusDays(3))
                .build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(validScore, invalidScore));

        var result = userScoreService.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTotalPoints() == 10)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return ranking in descending order")
    void givenExistingScores_whenGetRankingDescOrder_thenReturnsRankingDescending() {
        RankingResponseDto first = new RankingResponseDto("Juanito", 200);
        RankingResponseDto second = new RankingResponseDto("Pepito", 150);

        when(userScoreRepository.findUsersRanking()).thenReturn(Flux.just(first, second));

        StepVerifier.create(userScoreService.getRankingDescOrder())
                .expectNextMatches(r -> r.getUsername().equals("Juanito") && r.getPoints() == 200)
                .expectNextMatches(r -> r.getUsername().equals("Pepito") && r.getPoints() == 150)
                .verifyComplete();

        verify(userScoreRepository, times(1)).findUsersRanking();
    }

    @Test
    @DisplayName("Should return empty when no scores exist")
    void givenNoScores_whenGetRankingDescOrder_thenReturnsEmpty() {
        when(userScoreRepository.findUsersRanking()).thenReturn(Flux.empty());

        StepVerifier.create(userScoreService.getRankingDescOrder())
                .expectNextCount(0)
                .verifyComplete();

        verify(userScoreRepository, times(1)).findUsersRanking();
    }
}