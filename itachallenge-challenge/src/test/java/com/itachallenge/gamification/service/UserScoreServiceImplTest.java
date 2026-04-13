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
    private static final LocalDateTime WEEK_10_TUESDAY = LocalDateTime.of(2024, 3, 5, 10, 0);
    private static final LocalDateTime WEEK_10_SUNDAY = LocalDateTime.of(2024, 3, 10, 23, 59);
    private static final LocalDateTime WEEK_11_MONDAY = LocalDateTime.of(2024, 3, 11, 0, 1);

    private UserScoreDocument scoreDoc(int points, LocalDateTime date) {
        return UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .userId(userId)
                .pointsEarned(points)
                .challengeId(UUID.randomUUID())
                .createdAt(date)
                .build();
    }

    @Test
    void givenMultipleScores_whenGetUserPointsHistory_thenTotalPointsIsCorrectlySummed() {
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument score1 = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED).userId(userId).pointsEarned(10).challengeId(UUID.randomUUID()).createdAt(now).build();
        UserScoreDocument score2 = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED).userId(userId).pointsEarned(5).challengeId(UUID.randomUUID()).createdAt(now.minusDays(3)).build();
        UserScoreDocument score3 = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED).userId(userId).pointsEarned(20).challengeId(UUID.randomUUID()).createdAt(now.minusDays(1)).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Flux.just(score1, score2, score3));

        var result = userScoreService.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTotalPoints() == 35)
                .verifyComplete();
    }

    @Test
    void givenDescendingRepoData_whenGetUserPointsHistory_thenReturnsAscendingForFrontend() {
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument latest = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .userId(userId)
                .pointsEarned(10)
                .challengeId(UUID.randomUUID())
                .createdAt(now)
                .build();
        UserScoreDocument older = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .userId(userId)
                .pointsEarned(5)
                .challengeId(UUID.randomUUID())
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
        LocalDateTime fixedDate = LocalDateTime.of(2015, 3, 26, 7, 33, 21);
        String expectedDate = fixedDate.toString();

        UserScoreDocument score = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .userId(userId)
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
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument validScore = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .userId(userId)
                .challengeId(UUID.randomUUID())
                .pointsEarned(10)
                .createdAt(now)
                .build();
        UserScoreDocument invalidScore = UserScoreDocument.builder().activityType(ActivityType.CHALLENGE_COMPLETED)
                .userId(userId)
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
    void givenScores_whenGetUserScoresHistoryChart_thenPeriodFormatIsIsoWeek() {
        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(scoreDoc(50, WEEK_10_MONDAY)));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getHistory().getFirst().getPeriod().matches("\\d{4}-W\\d{2}"))
                .verifyComplete();
    }

    @Test
    void givenScoresAcrossMultipleWeeks_whenGetUserScoresHistoryChart_thenTotalPointsEqualsLastAccumulatedAtEnd() {
        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(
                        scoreDoc(150, WEEK_10_MONDAY),
                        scoreDoc(75, WEEK_11_MONDAY)
                ));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getTotalPoints() == response.getHistory()
                                .get(response.getHistory().size() - 1).getAccumulatedAtEnd())
                .verifyComplete();
    }

    @Test
    void givenNoScores_whenGetUserScoresHistoryChart_thenReturnsTotalPointsZeroAndEmptyHistory() {
        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.empty());

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getTotalPoints() == 0 &&
                                response.getHistory().isEmpty())
                .verifyComplete();
    }

    @SuppressWarnings("java:S2699") // to be extended with activityType when #275/#276 are merged
    @Test
    void givenMixedSourcesInSameWeek_whenGetUserScoresHistoryChart_thenAllPointsAreSummedTogether() {
        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(scoreDoc(30, WEEK_10_MONDAY),
                        scoreDoc(60, WEEK_10_TUESDAY)));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getHistory().size() == 1 &&
                                response.getHistory().getFirst().getPointsEarned() == 90)
                .verifyComplete();
    }

    @Test
    void givenScoresOnSundayAndNextMonday_whenGetUserScoresHistoryChart_thenBelongToDifferentWeeks() {
        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(
                        scoreDoc(50, WEEK_10_SUNDAY),
                        scoreDoc(75, WEEK_11_MONDAY)));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getHistory().size() == 2 &&
                                response.getHistory().get(0).getPeriod().equals("2024-W10") &&
                                response.getHistory().get(1).getPeriod().equals("2024-W11"))
                .verifyComplete();
    }

    @Test
    void givenValidInput_whenAssignPoints_thenReturnsPointsAndSavesDocument() {
        ActivityType activityType = ActivityType.CODE_REVIEW;

        when(userScoreRepository.save(any(UserScoreDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        var result = userScoreService.assignPoints(userId, activityType);

        StepVerifier.create(result)
                .expectNext(activityType.getPoints())
                .verifyComplete();

        verify(userScoreRepository).save(any());
    }

    @Test
    void givenNullUserId_whenAssignPoints_thenError() {

        ActivityType activityType = ActivityType.CODE_REVIEW;

        var result = userScoreService.assignPoints(null, activityType);

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
    void givenNullUsername_whenRegisterPoints_thenError() {
        UUID userId = UUID.randomUUID();

        var result = userScoreService.registerPoints(
                userId,
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
    void givenBlankUsername_whenRegisterPoints_thenError() {
        UUID userId = UUID.randomUUID();

        var result = userScoreService.registerPoints(
                userId,
                "   ",
                ActivityType.CODE_REVIEW,
                null
        );

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
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