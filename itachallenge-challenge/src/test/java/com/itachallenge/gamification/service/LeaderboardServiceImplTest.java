package com.itachallenge.gamification.service;

import com.itachallenge.gamification.repository.UserScoreAggregation;
import com.itachallenge.gamification.repository.UserScoreRepository;
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

    private UserScoreAggregation aggregate1;
    private UserScoreAggregation aggregate2;

    @BeforeEach
    void setUp() {
        aggregate1 = new UserScoreAggregation("testUser", 100);
        aggregate2 = new UserScoreAggregation(null, 50);
    }

    @Test
    void getLeaderboard_returnsSortedData() {
        when(userScoreRepository.aggregateUserScores()).thenReturn(Flux.just(aggregate1, aggregate2));

        StepVerifier.create(leaderboardServiceImpl.getLeaderboard())
                .assertNext(response -> {
                    assertThat(response.getLeaderboard()).hasSize(2);
                    assertThat(response.getLeaderboard().getFirst().getUsername()).isEqualTo("testUser");
                    assertThat(response.getLeaderboard().getFirst().getTotalPoints()).isEqualTo(100);
                    assertThat(response.getLeaderboard().get(1).getUsername()).isEqualTo("Anonymous");
                    assertThat(response.getLeaderboard().get(1).getTotalPoints()).isEqualTo(50);
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
}
