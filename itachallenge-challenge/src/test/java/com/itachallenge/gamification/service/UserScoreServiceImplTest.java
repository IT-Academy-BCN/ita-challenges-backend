package com.itachallenge.gamification.service;

import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.enums.ActivityType;
import com.itachallenge.gamification.repository.UserScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

        UserScoreDocument score1 = UserScoreDocument.builder()
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .pointsEarned(10)
                .challengeId(UUID.randomUUID())
                .createdAt(now)
                .build();

        UserScoreDocument score2 = UserScoreDocument.builder()
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .pointsEarned(5)
                .challengeId(UUID.randomUUID())
                .createdAt(now.minusDays(3))
                .build();

        UserScoreDocument score3 = UserScoreDocument.builder()
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .pointsEarned(20)
                .challengeId(UUID.randomUUID())
                .createdAt(now.minusDays(1))
                .build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(score1, score2, score3));

        var result = userScoreService.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTotalPoints() == 35)
                .verifyComplete();
    }

    @Test
    void givenDescendingRepoData_whenGetUserPointsHistory_thenReturnsAscendingForFrontend() {
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument latest = UserScoreDocument.builder()
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .pointsEarned(10)
                .createdAt(now)
                .build();

        UserScoreDocument older = UserScoreDocument.builder()
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .pointsEarned(5)
                .createdAt(now.minusDays(3))
                .build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(latest, older));

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
                .activityType(ActivityType.CHALLENGE_COMPLETED)
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

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.empty());

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
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .challengeId(UUID.randomUUID())
                .pointsEarned(10)
                .createdAt(now)
                .build();

        UserScoreDocument invalidScore = UserScoreDocument.builder()
                .activityType(ActivityType.CHALLENGE_COMPLETED)
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
    void givenChallengeCompleted_whenRegisterPoints_thenSavesWithChallengeId() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        when(userScoreRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = userScoreService.registerPoints(userId, "testUser", ActivityType.CHALLENGE_COMPLETED, challengeId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(userScoreRepository).save(any());
    }

    @Test
    void givenChallengeCompletedWithoutChallengeId_whenRegisterPoints_thenError() {
        UUID userId = UUID.randomUUID();

        var result = userScoreService.registerPoints(userId, "testUser", ActivityType.CHALLENGE_COMPLETED, null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    @Test
    void givenNonChallengeWithChallengeId_whenRegisterPoints_thenError() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        var result = userScoreService.registerPoints(userId, "testUser", ActivityType.CODE_REVIEW, challengeId);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    @Test
    void givenNonChallengeActivity_whenRegisterPoints_thenSavesWithoutChallengeId() {
        UUID userId = UUID.randomUUID();

        when(userScoreRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = userScoreService.registerPoints(userId, "testUser", ActivityType.CODE_REVIEW, null);

        StepVerifier.create(result)
                .verifyComplete();

        verify(userScoreRepository).save(any());
    }
}