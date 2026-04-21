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

    private UserScoreDocument scoreDoc(int points, LocalDateTime date) {
        return UserScoreDocument.builder()
                .userId(userId)
                .pointsEarned(points)
                .challengeId(UUID.randomUUID())
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
}