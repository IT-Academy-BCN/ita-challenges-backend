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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    void getWeeklyLeagues_splitsIntoGoldSilverBronze_preservingRepositoryOrder() {
        // Order matches Mongo aggregation: totalPoints desc, username asc (same as DB $sort)
        List<LeaderboardAggregationResult> asReturnedByDb = new ArrayList<>();
        asReturnedByDb.add(new LeaderboardAggregationResult("anna", 100));
        asReturnedByDb.add(new LeaderboardAggregationResult("zoe", 100));
        asReturnedByDb.add(new LeaderboardAggregationResult("mike", 90));
        for (int i = 1; i <= 30; i++) {
            asReturnedByDb.add(new LeaderboardAggregationResult("user" + i, 89 - i));
        }

        when(userScoreRepository.aggregateUserScoresByPeriod(org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(Flux.fromIterable(asReturnedByDb));

        StepVerifier.create(leaderboardServiceImpl.getWeeklyLeagues())
                .assertNext(response -> {
                    assertThat(response.getGold()).hasSize(10);
                    assertThat(response.getSilver()).hasSize(20);
                    assertThat(response.getBronze()).hasSize(3);

                    // Service preserves repository order and only splits into leagues.
                    assertThat(response.getGold().get(0).getUsername()).isEqualTo("anna");
                    assertThat(response.getGold().get(1).getUsername()).isEqualTo("zoe");
                    assertThat(response.getGold().get(2).getUsername()).isEqualTo("mike");
                })
                .verifyComplete();
    }

    @Test
    void getWeeklyLeagues_whenRepositoryFails_throwsInternalServiceException() {
        when(userScoreRepository.aggregateUserScoresByPeriod(org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(Flux.error(new RuntimeException("Database connection failed.")));

        StepVerifier.create(leaderboardServiceImpl.getWeeklyLeagues())
                .expectErrorSatisfies(throwable -> {
                    assertThat(throwable)
                            .isInstanceOf(InternalServerErrorException.class)
                            .hasMessage("Unable to retrieve weekly leagues data. Please try again later.");
                })
                .verify();
    }

    @Test
    void getWeeklyLeagues_whenLessThanTenEntries_keepsSilverAndBronzeEmpty() {
        when(userScoreRepository.aggregateUserScoresByPeriod(org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(Flux.fromIterable(createOrderedAggregations(5)));

        StepVerifier.create(leaderboardServiceImpl.getWeeklyLeagues())
                .assertNext(response -> {
                    assertThat(response.getGold()).hasSize(5);
                    assertThat(response.getSilver()).isEmpty();
                    assertThat(response.getBronze()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void getWeeklyLeagues_whenExactlyTenEntries_putsAllUsersInGoldOnly() {
        when(userScoreRepository.aggregateUserScoresByPeriod(org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(Flux.fromIterable(createOrderedAggregations(10)));

        StepVerifier.create(leaderboardServiceImpl.getWeeklyLeagues())
                .assertNext(response -> {
                    assertThat(response.getGold()).hasSize(10);
                    assertThat(response.getSilver()).isEmpty();
                    assertThat(response.getBronze()).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    void getWeeklyLeagues_whenExactlyThirtyEntries_putsUsersInGoldAndSilverOnly() {
        when(userScoreRepository.aggregateUserScoresByPeriod(org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(Flux.fromIterable(createOrderedAggregations(30)));

        StepVerifier.create(leaderboardServiceImpl.getWeeklyLeagues())
                .assertNext(response -> {
                    assertThat(response.getGold()).hasSize(10);
                    assertThat(response.getSilver()).hasSize(20);
                    assertThat(response.getBronze()).isEmpty();
                })
                .verifyComplete();
    }

    private List<LeaderboardAggregationResult> createOrderedAggregations(int totalUsers) {
        List<LeaderboardAggregationResult> ordered = new ArrayList<>();
        for (int i = 0; i < totalUsers; i++) {
            ordered.add(new LeaderboardAggregationResult("user" + i, 100 - i));
        }
        return ordered;
    }
}
