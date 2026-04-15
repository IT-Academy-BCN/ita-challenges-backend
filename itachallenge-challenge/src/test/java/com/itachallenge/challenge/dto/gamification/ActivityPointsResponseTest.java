package com.itachallenge.challenge.dto.gamification;

import com.itachallenge.gamification.enums.ActivityType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityPointsResponseTest {

    @Test
    void shouldCreateResponseWithAllArgsConstructor() {
        ActivityPointsResponse response =
                new ActivityPointsResponse(10, ActivityType.CODE_REVIEW);

        assertThat(response.getPointsEarned()).isEqualTo(10);
        assertThat(response.getActivityType()).isEqualTo(ActivityType.CODE_REVIEW);
    }

    @Test
    void shouldSetAndGetValues() {
        ActivityPointsResponse response =
                new ActivityPointsResponse(0, null);

        response.setPointsEarned(15);
        response.setActivityType(ActivityType.PRESENTATION);

        assertThat(response.getPointsEarned()).isEqualTo(15);
        assertThat(response.getActivityType()).isEqualTo(ActivityType.PRESENTATION);
    }

    @Test
    void shouldSupportEqualsAndHashCode() {
        ActivityPointsResponse r1 =
                new ActivityPointsResponse(5, ActivityType.CODE_REVIEW);

        ActivityPointsResponse r2 =
                new ActivityPointsResponse(5, ActivityType.CODE_REVIEW);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1).hasSameHashCodeAs(r2);
    }

    @Test
    void shouldNotBeEqualWhenFieldsDiffer() {
        ActivityPointsResponse r1 =
                new ActivityPointsResponse(5, ActivityType.CODE_REVIEW);

        ActivityPointsResponse r2 =
                new ActivityPointsResponse(10, ActivityType.CODE_REVIEW);

        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentObject() {
        ActivityPointsResponse response =
                new ActivityPointsResponse(5, ActivityType.CODE_REVIEW);

        assertThat(response).isNotEqualTo(null);
        assertThat(response).isNotEqualTo("string");
    }

    @Test
    void shouldOverwriteValuesWithSetters() {
        ActivityPointsResponse response =
                new ActivityPointsResponse(5, ActivityType.CODE_REVIEW);

        response.setPointsEarned(10);
        response.setPointsEarned(20);

        assertThat(response.getPointsEarned()).isEqualTo(20);
    }

    @Test
    void shouldHaveToString() {
        ActivityPointsResponse response =
                new ActivityPointsResponse(5, ActivityType.CODE_REVIEW);

        assertThat(response.toString())
                .contains("5")
                .contains("CODE_REVIEW");
    }
}