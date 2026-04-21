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

    private final UUID userId = UUID.randomUUID();
    private static final LocalDateTime WEEK_10_MONDAY = LocalDateTime.of(2024, 3, 4, 10, 0);
    private static final LocalDateTime WEEK_10_WEDNESDAY = LocalDateTime.of(2024, 3, 6, 10, 0);
    private static final LocalDateTime WEEK_11_MONDAY = LocalDateTime.of(2024, 3, 11, 0, 1);

    private UserScoreDocument scoreDoc(int points, LocalDateTime date) {
        return UserScoreDocument.builder()
                .userId(userId)
                .pointsEarned(points)
                .challengeId(UUID.randomUUID())
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .createdAt(date)
                .build();
    }

   

    @Test
    void givenScoresInSameWeek_whenGetUserScoresHistoryChart_thenWeeklyPointsEarnedIsCorrect() {
        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(
                        scoreDoc(50, WEEK_10_MONDAY),
                        scoreDoc(100, WEEK_10_WEDNESDAY)));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getHistory().size() == 1 &&
                                response.getHistory().getFirst().getPointsEarned() == 150)
                .verifyComplete();
    }

    @Test
    void givenScoresAcrossMultipleWeeks_whenGetUserScoresHistoryChart_thenAccumulatedAtEndIsProgressiveSum() {
        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(
                        scoreDoc(150, WEEK_10_MONDAY),
                        scoreDoc(75, WEEK_11_MONDAY)
                ));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getHistory().get(0).getAccumulatedAtEnd() == 150 &&
                                response.getHistory().get(1).getAccumulatedAtEnd() == 225)
                .verifyComplete();
    }

    @Test
    void givenChallengeCompleted_whenRegisterPoints_thenSavesWithChallengeId() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        when(userScoreRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = userScoreService.registerPoints(userId, ActivityType.CHALLENGE_COMPLETED, challengeId);

        StepVerifier.create(result).verifyComplete();

        verify(userScoreRepository, times(1)).save(any());
    }

    @Test
    void givenChallengeCompletedWithoutChallengeId_whenRegisterPoints_thenError() {
        UUID userId = UUID.randomUUID();

        var result = userScoreService.registerPoints(userId, ActivityType.CHALLENGE_COMPLETED, null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    @Test
    void givenNonChallengeWithChallengeId_whenRegisterPoints_thenError() {
        UUID userId = UUID.randomUUID();

        var result = userScoreService.registerPoints(userId, ActivityType.CODE_REVIEW, UUID.randomUUID());

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

        var result = userScoreService.registerPoints(userId, ActivityType.CODE_REVIEW, null);

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
        assertThat(saved.getUsername()).isNull();
    }

    @Test
    void givenScores_whenGetUserScoresHistoryChart_thenPeriodFormatIsIsoWeek() {
        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(scoreDoc(50, WEEK_10_MONDAY)));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getHistory().getFirst().getPeriod().matches("\\d{4}-W\\d{2}"))
                .verifyComplete();
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
                null,
                null
        );

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    @Test
    void givenValidNonChallengeInput_whenRegisterPoints_thenSucceeds() {
        UUID userId = UUID.randomUUID();

        when(userScoreRepository.save(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = userScoreService.registerPoints(
                userId,
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
                ActivityType.CODE_REVIEW,
                null
        );

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
