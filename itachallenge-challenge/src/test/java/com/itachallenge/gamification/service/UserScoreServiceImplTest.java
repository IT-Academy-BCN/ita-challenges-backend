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

    private final UUID userId = UUID.randomUUID();
    private static final LocalDateTime WEEK_10_MONDAY    = LocalDateTime.of(2024, 3, 4,  10, 0);
    private static final LocalDateTime WEEK_10_WEDNESDAY = LocalDateTime.of(2024, 3, 6,  10, 0);
    private static final LocalDateTime WEEK_10_TUESDAY   = LocalDateTime.of(2024, 3, 5,  10, 0);
    private static final LocalDateTime WEEK_10_SUNDAY    = LocalDateTime.of(2024, 3, 10, 23, 59);
    private static final LocalDateTime WEEK_11_MONDAY    = LocalDateTime.of(2024, 3, 11, 0, 1);

    @Test
    void givenMultipleScores_whenGetUserPointsHistory_thenTotalPointsIsCorrectlySummed() {
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
    void givenScoresInSameWeek_whenGetUserScoresHistoryChart_thenWeeklyPointsEarnedIsCorrect() {

        UserScoreDocument score1 = UserScoreDocument.builder()
                .userId(userId).pointsEarned(50).challengeId(UUID.randomUUID()).createdAt(WEEK_10_MONDAY).build();
        UserScoreDocument score2 = UserScoreDocument.builder()
                .userId(userId).pointsEarned(100).challengeId(UUID.randomUUID()).createdAt(WEEK_10_WEDNESDAY).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(score1, score2));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getHistory().size() == 1 &&
                                response.getHistory().getFirst().getPointsEarned() == 150)
                .verifyComplete();
    }

    @Test
    void givenScoresAcrossMultipleWeeks_whenGetUserScoresHistoryChart_thenAccumulatedAtEndIsProgressiveSum() {
        UserScoreDocument score1 = UserScoreDocument.builder()
                .userId(userId).pointsEarned(150).challengeId(UUID.randomUUID()).createdAt(WEEK_10_MONDAY).build();
        UserScoreDocument score2 = UserScoreDocument.builder()
                .userId(userId).pointsEarned(75).challengeId(UUID.randomUUID()).createdAt(WEEK_11_MONDAY).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(score1, score2));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getHistory().get(0).getAccumulatedAtEnd() == 150 &&
                                response.getHistory().get(1).getAccumulatedAtEnd() == 225)
                .verifyComplete();
    }

    @Test
    void givenScores_whenGetUserScoresHistoryChart_thenPeriodFormatIsIsoWeek() {
        UserScoreDocument score = UserScoreDocument.builder()
                .userId(userId).pointsEarned(50).challengeId(UUID.randomUUID()).createdAt(WEEK_10_MONDAY).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(score));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getHistory().getFirst().getPeriod().matches("\\d{4}-W\\d{2}"))
                .verifyComplete();
    }

    @Test
    void givenScoresAcrossMultipleWeeks_whenGetUserScoresHistoryChart_thenTotalPointsEqualsLastAccumulatedAtEnd() {
        UserScoreDocument score1 = UserScoreDocument.builder()
                .userId(userId).pointsEarned(150).challengeId(UUID.randomUUID()).createdAt(WEEK_10_MONDAY).build();
        UserScoreDocument score2 = UserScoreDocument.builder()
                .userId(userId).pointsEarned(75).challengeId(UUID.randomUUID()).createdAt(WEEK_11_MONDAY).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(score1, score2));

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
        UserScoreDocument source1 = UserScoreDocument.builder()
                .userId(userId).pointsEarned(30).challengeId(UUID.randomUUID()).createdAt(WEEK_10_MONDAY).build();
        UserScoreDocument source2 = UserScoreDocument.builder()
                .userId(userId).pointsEarned(60).challengeId(UUID.randomUUID()).createdAt(WEEK_10_TUESDAY).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(source1, source2));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getHistory().size() == 1 &&
                                response.getHistory().getFirst().getPointsEarned() == 90)
                .verifyComplete();
    }

    @Test
    void givenScoresOnSundayAndNextMonday_whenGetUserScoresHistoryChart_thenBelongToDifferentWeeks() {
        UserScoreDocument sundayScore = UserScoreDocument.builder()
                .userId(userId).pointsEarned(50).challengeId(UUID.randomUUID()).createdAt(WEEK_10_SUNDAY).build();
        UserScoreDocument mondayScore = UserScoreDocument.builder()
                .userId(userId).pointsEarned(75).challengeId(UUID.randomUUID()).createdAt(WEEK_11_MONDAY).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(Flux.just(sundayScore, mondayScore));

        StepVerifier.create(userScoreService.getUserScoresHistoryChart(userId))
                .expectNextMatches(response ->
                        response.getHistory().size() == 2 &&
                                response.getHistory().get(0).getPeriod().equals("2024-W10") &&
                                response.getHistory().get(1).getPeriod().equals("2024-W11"))
                .verifyComplete();
    }
}