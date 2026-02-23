package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointHistoryEntryDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import com.itachallenge.gamification.mapper.GamificationMapper;
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
class PointsServiceImplTest {

    @Mock
    private UserScoreRepository userScoreRepository;

    @Mock
    private GamificationMapper gamificationMapper;

    @InjectMocks
    private PointsServiceImpl pointsServiceImpl;

    @Test
    void givenMultipleScores_whenGetUserPointsHistory_thenTotalPointsIsCorrectlySummed() {
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument score1 = UserScoreDocument.builder().points(10).challengeId(UUID.randomUUID()).createdAt(now).build();
        UserScoreDocument score2 = UserScoreDocument.builder().points(5).challengeId(UUID.randomUUID()).createdAt(now.minusDays(3)).build();
        UserScoreDocument score3 = UserScoreDocument.builder().points(20).challengeId(UUID.randomUUID()).createdAt(now.minusDays(1)).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Flux.just(score1, score2, score3));

        var result = pointsServiceImpl.getUserPointsHistory(userId);

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

        UserScoreDocument score = UserScoreDocument.builder().points(10).challengeId(UUID.randomUUID()).createdAt(fixedDate).build();
        PointHistoryEntryDto expectedDto = PointHistoryEntryDto.builder().points(10).createdAt(expectedDate).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(score));
        when(gamificationMapper.toPointEntryDto(score)).thenReturn(expectedDto);

        var result = pointsServiceImpl.getUserPointsHistory(userId);

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

        var result = pointsServiceImpl.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getTotalPoints() == 0 &&
                                response.getHistory().isEmpty())
                .verifyComplete();
    }

    @Test
    void givenScoresWithNullPoints_whenGetUserPointsHistory_thenTotalPointsIgnoresNullValues() {
        UUID userId = UUID.randomUUID();

        UserScoreDocument validScore = UserScoreDocument.builder().challengeId(UUID.randomUUID()).points(10).build();
        UserScoreDocument invalidScore = UserScoreDocument.builder().challengeId(UUID.randomUUID()).points(null).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(validScore, invalidScore));

        var result = pointsServiceImpl.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTotalPoints() == 10)
                .verifyComplete();
    }

    @Test
    void givenValidScores_whenGetUserPointsHistory_thenHistoryIsReturnedInCorrectOrder() {
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        UserScoreDocument latestScore = UserScoreDocument.builder().points(10).challengeId(UUID.randomUUID()).createdAt(now).build();
        UserScoreDocument olderScore = UserScoreDocument.builder().points(5).challengeId(UUID.randomUUID()).createdAt(now.minusDays(3)).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Flux.just(olderScore, latestScore));

        when(gamificationMapper.toPointEntryDto(latestScore))
                .thenReturn(PointHistoryEntryDto.builder().points(10).createdAt(now.toString()).build());
        when(gamificationMapper.toPointEntryDto(olderScore))
                .thenReturn(PointHistoryEntryDto.builder().points(5).createdAt(now.minusDays(3).toString()).build());

        var result = pointsServiceImpl.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getHistory().size() == 2 &&
                                response.getHistory().getFirst().getPoints() == 5 &&
                                response.getHistory().get(1).getPoints() == 10)
                .verifyComplete();
    }

    @Test
    void givenDuplicateChallengesScores_whenGetUserPointsHistory_thenDistinctFilterApplies() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        UserScoreDocument score1 = UserScoreDocument.builder().points(10).challengeId(challengeId).build();
        UserScoreDocument score2 = UserScoreDocument.builder().points(10).challengeId(challengeId).build();

        when(userScoreRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Flux.just(score1, score2));

        var result = pointsServiceImpl.getUserPointsHistory(userId);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getHistory().size() == 1 &&
                        response.getTotalPoints() == 10)
                .verifyComplete();
    }
}