package com.itachallenge.gamification.repository;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserScoreAggregationTest {

    @Test
    void testUserScoreAggregation() {
        String username = "user";
        Integer totalPoints = 100;

        UserScoreAggregation agg = new UserScoreAggregation(username, totalPoints);

        assertThat(agg.getUsername()).isEqualTo("user");
        assertThat(agg.getTotalPoints()).isEqualTo(100);
    }

    @Test
    void testUserScoreAggregationBuilder() {
        UserScoreAggregation agg = UserScoreAggregation.builder()
                .username("testUser")
                .totalPoints(150)
                .build();

        assertThat(agg.getUsername()).isEqualTo("testUser");
        assertThat(agg.getTotalPoints()).isEqualTo(150);
    }
}
