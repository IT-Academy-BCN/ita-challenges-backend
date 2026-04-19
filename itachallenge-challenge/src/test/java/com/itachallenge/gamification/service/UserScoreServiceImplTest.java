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
        return UserScoreDocument.builder()
                .activityType(ActivityType.CHALLENGE_COMPLETED)
                .userId(userId)
                .pointsEarned(points)
                .challengeId(UUID.randomUUID())
                .createdAt(date)
                .build();
    }

    // =========================
    // HISTORY TESTS
    // =========================

    @Test
    void givenMultipleScores_whenGetUserPointsHistory_thenTotalPointsIsCorrectlySummed() {
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument score1 = scoreDoc(10, now);
        UserScoreDocument score2 = scoreDoc(5, now.minusDays(3));
        UserScoreDocument score3 = scoreDoc(20, now.minusDays(1));

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(score1, score2, score3));

        StepVerifier.create(userScoreService.getUserPointsHistory(userId))
                .expectNextMatches(r -> r.getTotalPoints() == 35)
                .verifyComplete();
    }

    @Test
    void givenNoScores_whenGetUserPointsHistory_thenReturnsEmpty() {
        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.empty());

        StepVerifier.create(userScoreService.getUserPointsHistory(userId))
                .expectNextMatches(r -> r.getTotalPoints() == 0 && r.getHistory().isEmpty())
                .verifyComplete();
    }

    // =========================
    // CHART TESTS
    // =========================

    @Test
    void givenScoresInSameWeek_whenGetChart_thenAggregatesCorrectly() {
        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(
                        scoreDoc(50, WEEK_10_MONDAY),
                        scoreDoc(100, WEEK_10_WEDNESDAY)));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(r ->
                        r.getHistory().size() == 1 &&
                                r.getHistory().getFirst().getPointsEarned() == 150)
                .verifyComplete();
    }

    @Test
    void givenScoresAcrossWeeks_whenGetChart_thenAccumulatedIsCorrect() {
        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(
                        scoreDoc(150, WEEK_10_MONDAY),
                        scoreDoc(75, WEEK_11_MONDAY)));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(r ->
                        r.getHistory().get(1).getAccumulatedAtEnd() == 225)
                .verifyComplete();
    }

    // =========================
    // ASSIGN POINTS
    // =========================

    @Test
    void givenValidInput_whenAssignPoints_thenReturnsPoints() {
        ActivityType type = ActivityType.CODE_REVIEW;

        when(userScoreRepository.save(any()))
                .thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(userScoreService.assignPoints(userId, type))
                .expectNext(type.getPoints())
                .verifyComplete();

        verify(userScoreRepository).save(any());
    }

    @Test
    void givenNullUserId_whenAssignPoints_thenError() {
        StepVerifier.create(userScoreService.assignPoints(null, ActivityType.CODE_REVIEW))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    @Test
    void givenNullType_whenAssignPoints_thenError() {
        StepVerifier.create(userScoreService.assignPoints(userId, null))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    // =========================
    // REGISTER POINTS
    // =========================

    @Test
    void givenValidInput_whenRegisterPoints_thenSaved() {
        when(userScoreRepository.save(any()))
                .thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(userScoreService.registerPoints(
                        userId,
                        ActivityType.CODE_REVIEW,
                        null))
                .verifyComplete();

        verify(userScoreRepository).save(any());
    }

    @Test
    void givenNullUserId_whenRegisterPoints_thenError() {
        StepVerifier.create(userScoreService.registerPoints(
                        null,
                        ActivityType.CODE_REVIEW,
                        null))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    @Test
    void givenNullType_whenRegisterPoints_thenError() {
        StepVerifier.create(userScoreService.registerPoints(
                        userId,
                        null,
                        null))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(userScoreRepository);
    }

    @Test
    void givenChallengeWithoutId_whenRegisterPoints_thenError() {
        StepVerifier.create(userScoreService.registerPoints(
                        userId,
                        ActivityType.CHALLENGE_COMPLETED,
                        null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void givenNonChallengeWithId_whenRegisterPoints_thenError() {
        StepVerifier.create(userScoreService.registerPoints(
                        userId,
                        ActivityType.CODE_REVIEW,
                        UUID.randomUUID()))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}