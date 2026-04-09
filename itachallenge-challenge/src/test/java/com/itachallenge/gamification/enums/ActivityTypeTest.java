package com.itachallenge.gamification.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityTypeTest {

    @Test
    void shouldExposeExpectedActivityTypesInDeclaredOrder() {
        assertThat(ActivityType.values())
                .containsExactly(
                        ActivityType.CODE_REVIEW,
                        ActivityType.PRESENTATION,
                        ActivityType.CHALLENGE_COMPLETED
                );
    }

    @Test
    void shouldExposeConfiguredPointsForEachActivityType() {
        assertThat(ActivityType.CODE_REVIEW.getPoints()).isEqualTo(5);
        assertThat(ActivityType.PRESENTATION.getPoints()).isEqualTo(10);
        assertThat(ActivityType.CHALLENGE_COMPLETED.getPoints()).isEqualTo(20);
    }
}
