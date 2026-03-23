package com.itachallenge.gamification.service;

import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.gamification.repository.UserScoreRepository;
import com.itachallenge.gamification.repository.projection.LeaderboardAggregationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceImplTest {

    @Mock
    private UserScoreRepository userScoreRepository;

    @InjectMocks
    private LeaderboardServiceImpl leaderboardServiceImpl;

    private LeaderboardAggregationResult aggregate1;
    private LeaderboardAggregationResult aggregate2;

    @BeforeEach
    void setUp() {
        aggregate1 = new LeaderboardAggregationResult("testUser", 100);
        aggregate2 = new LeaderboardAggregationResult(null, 50);
    }

    @Test
    void getLeaderboard_mapsAggregationsWithFallbacks() {
        when(userScoreRepository.aggregateUserScores()).thenReturn(Flux.just(aggregate1, aggregate2));

        StepVerifier.create(leaderboardServiceImpl.getLeaderboard())
                .assertNext(response -> {
                    assertThat(response.getLeaderboard())
                            .hasSize(2)
                            .satisfiesExactlyInAnyOrder(
                                    first -> {
                                        assertThat(first.getUsername()).isEqualTo("testUser");
                                        assertThat(first.getTotalPoints()).isEqualTo(100);
                                    },
                                    second -> {
                                        assertThat(second.getUsername()).isEqualTo("Anonymous");
                                        assertThat(second.getTotalPoints()).isEqualTo(50);
                                    }
                            );
                })
                .verifyComplete();
    }

    @Test
    void getLeaderboard_whenEmpty_returnsEmptyList() {
        when(userScoreRepository.aggregateUserScores()).thenReturn(Flux.empty());

        StepVerifier.create(leaderboardServiceImpl.getLeaderboard())
                .assertNext(response -> {
                    assertThat(response.getLeaderboard()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void getLeaderboard_whenRepositoryFails_throwsInternalServiceException() {
        when(userScoreRepository.aggregateUserScores())
                .thenReturn(Flux.error(new RuntimeException("Database connection failed.")));

        StepVerifier.create(leaderboardServiceImpl.getLeaderboard())
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable)
                            .isInstanceOf(InternalServerErrorException.class)
                            .hasMessage("Unable to retrieve leaderboard data. Please try again later.");
                })
                .verify();
    }
}
