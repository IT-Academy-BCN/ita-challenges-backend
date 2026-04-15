package com.itachallenge.gamification.service;

import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.enums.ActivityType;
import com.itachallenge.gamification.repository.UserScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void givenChallengeCompleted_whenRegisterPoints_thenSavesWithChallengeId() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        when(userScoreRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = userScoreService.registerPoints(userId, "testUser", ActivityType.CHALLENGE_COMPLETED, challengeId);

        StepVerifier.create(result).verifyComplete();

        verify(userScoreRepository, times(1)).save(any());
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

        var result = userScoreService.registerPoints(userId, "testUser", ActivityType.CODE_REVIEW, UUID.randomUUID());

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

        StepVerifier.create(result).verifyComplete();

        verify(userScoreRepository, times(1)).save(any());
    }

    @Test
    void givenValidInput_whenAssignPoints_thenReturnsPointsAndSavesCorrectDocument() {
        UUID userId = UUID.randomUUID();
        ActivityType activityType = ActivityType.CODE_REVIEW;

        when(userScoreRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = userScoreService.assignPoints(userId, activityType);

        StepVerifier.create(result)
                .expectNext(activityType.getPoints())
                .verifyComplete();

        ArgumentCaptor<UserScoreDocument> captor =
                ArgumentCaptor.forClass(UserScoreDocument.class);

        verify(userScoreRepository).save(captor.capture());

        UserScoreDocument saved = captor.getValue();

        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getActivityType()).isEqualTo(activityType);
        assertThat(saved.getPointsEarned()).isEqualTo(activityType.getPoints());
        assertThat(saved.getUsername()).isEqualTo("system");
    }

    @Test
    void givenNullUserId_whenAssignPoints_thenError() {
        var result = userScoreService.assignPoints(null, ActivityType.CODE_REVIEW);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    @Test
    void givenNullActivityType_whenAssignPoints_thenError() {
        UUID userId = UUID.randomUUID();

        var result = userScoreService.assignPoints(userId, null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    @Test
    void givenRepositoryError_whenAssignPoints_thenErrorPropagates() {
        UUID userId = UUID.randomUUID();
        ActivityType activityType = ActivityType.CODE_REVIEW;

        when(userScoreRepository.save(any()))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        var result = userScoreService.assignPoints(userId, activityType);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void givenDifferentActivityType_whenAssignPoints_thenReturnsCorrectPoints() {
        UUID userId = UUID.randomUUID();
        ActivityType activityType = ActivityType.PRESENTATION;

        when(userScoreRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = userScoreService.assignPoints(userId, activityType);

        StepVerifier.create(result)
                .expectNext(activityType.getPoints())
                .verifyComplete();
    }

    @Test
    void givenNullUserId_whenRegisterPoints_thenError() {
        var result = userScoreService.registerPoints(
                null,
                "testUser",
                ActivityType.CODE_REVIEW,
                null
        );

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    @Test
    void givenNullActivityType_whenRegisterPoints_thenError() {
        UUID userId = UUID.randomUUID();

        var result = userScoreService.registerPoints(
                userId,
                "testUser",
                null,
                null
        );

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    @Test
    void givenNullUsername_whenRegisterPoints_thenSucceeds() {
        UUID userId = UUID.randomUUID();

        when(userScoreRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = userScoreService.registerPoints(
                userId,
                null,
                ActivityType.CODE_REVIEW,
                null
        );

        StepVerifier.create(result)
                .verifyComplete();

        verify(userScoreRepository).save(any());
    }

    @Test
    void givenBlankUsername_whenRegisterPoints_thenSucceeds() {
        UUID userId = UUID.randomUUID();

        when(userScoreRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = userScoreService.registerPoints(
                userId,
                "   ",
                ActivityType.CODE_REVIEW,
                null
        );

        StepVerifier.create(result)
                .verifyComplete();

        verify(userScoreRepository).save(any());
    }

    @Test
    void givenRepositoryError_whenRegisterPoints_thenErrorPropagates() {
        UUID userId = UUID.randomUUID();

        when(userScoreRepository.save(any()))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        var result = userScoreService.registerPoints(
                userId,
                "testUser",
                ActivityType.CODE_REVIEW,
                null
        );

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}