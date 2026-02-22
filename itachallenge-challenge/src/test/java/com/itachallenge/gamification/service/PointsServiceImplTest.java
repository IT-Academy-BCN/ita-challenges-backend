package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.RankingResponseDto;
import com.itachallenge.gamification.repository.UserScoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointsServiceImplTest {

    @Mock
    private UserScoreRepository userScoreRepository;

    @InjectMocks
    private PointsServiceImpl pointsService;

    @Test
    @DisplayName("Should return ranking in descending order")
    void givenExistingScores_whenGetRankingDescOrder_thenReturnsRankingDescending() {
        RankingResponseDto first = new RankingResponseDto("Juanito", 200);
        RankingResponseDto second = new RankingResponseDto("Pepito", 150);

        when(userScoreRepository.findUsersRanking()).thenReturn(Flux.just(first, second));

        StepVerifier.create(pointsService.getRankingDescOrder())
                .expectNextMatches(r -> r.getUsername().equals("Juanito") && r.getPoints() == 200)
                .expectNextMatches(r -> r.getUsername().equals("Pepito") && r.getPoints() == 150)
                .verifyComplete();

        verify(userScoreRepository, times(1)).findUsersRanking();
    }

    @Test
    @DisplayName("Should return empty when no scores exist")
    void givenNoScores_whenGetRankingDescOrder_thenReturnsEmpty() {
        when(userScoreRepository.findUsersRanking()).thenReturn(Flux.empty());

        StepVerifier.create(pointsService.getRankingDescOrder())
                .expectNextCount(0)
                .verifyComplete();

        verify(userScoreRepository, times(1)).findUsersRanking();
    }
}