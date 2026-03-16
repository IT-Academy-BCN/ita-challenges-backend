package com.itachallenge.gamification.repository;


import com.itachallenge.gamification.repository.projection.LeaderboardAggregationResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LeaderboardAggregationResultTest {

    @Test
    void testUserScoreAggregation() {
        String username = "user";
        Integer totalPoints = 100;

        LeaderboardAggregationResult agg = new LeaderboardAggregationResult(username, totalPoints);

        assertThat(agg.getUsername()).isEqualTo("user");
        assertThat(agg.getTotalPoints()).isEqualTo(100);
    }

    @Test
    void testUserScoreAggregationBuilder() {
        LeaderboardAggregationResult agg = LeaderboardAggregationResult.builder()
                .username("testUser")
                .totalPoints(150)
                .build();

        assertThat(agg.getUsername()).isEqualTo("testUser");
        assertThat(agg.getTotalPoints()).isEqualTo(150);
    }
}
