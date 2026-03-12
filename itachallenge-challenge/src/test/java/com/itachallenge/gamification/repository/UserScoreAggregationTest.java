package com.itachallenge.gamification.repository;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserScoreAggregationTest {

    @Test
    void testUserScoreAggregation() {
        UserScoreAggregation agg =  new UserScoreAggregation("user", 100);
        agg.setUsername("newName");
        agg.setTotalPoints(200);

        assertThat(agg.getUsername()).isEqualTo("newName");
        assertThat(agg.getTotalPoints()).isEqualTo(200);
    }
}
