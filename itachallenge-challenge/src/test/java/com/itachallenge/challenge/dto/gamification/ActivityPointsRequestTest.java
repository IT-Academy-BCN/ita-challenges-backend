package com.itachallenge.challenge.dto.gamification;

import com.itachallenge.gamification.enums.ActivityType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityPointsRequestTest {

    @Test
    void shouldCreateRequestWithAllArgsConstructor() {
        UUID userId = UUID.randomUUID();
        ActivityType activityType = ActivityType.CODE_REVIEW;

        ActivityPointsRequest request = new ActivityPointsRequest(userId, activityType);

        assertThat(request.getUserId()).isEqualTo(userId);
        assertThat(request.getActivityType()).isEqualTo(activityType);
    }

    @Test
    void shouldSetAndGetValues() {
        ActivityPointsRequest request = new ActivityPointsRequest(null, null);

        UUID userId = UUID.randomUUID();
        ActivityType activityType = ActivityType.PRESENTATION;

        request.setUserId(userId);
        request.setActivityType(activityType);

        assertThat(request.getUserId()).isEqualTo(userId);
        assertThat(request.getActivityType()).isEqualTo(activityType);
    }

    @Test
    void shouldSupportEqualsAndHashCode() {
        UUID userId = UUID.randomUUID();

        ActivityPointsRequest r1 = new ActivityPointsRequest(userId, ActivityType.CODE_REVIEW);
        ActivityPointsRequest r2 = new ActivityPointsRequest(userId, ActivityType.CODE_REVIEW);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    void shouldHaveToString() {
        ActivityPointsRequest request =
                new ActivityPointsRequest(UUID.randomUUID(), ActivityType.CODE_REVIEW);

        assertThat(request.toString()).isNotNull();
    }
}