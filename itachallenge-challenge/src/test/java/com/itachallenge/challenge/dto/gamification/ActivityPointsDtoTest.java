package com.itachallenge.challenge.dto.gamification;

import com.itachallenge.gamification.enums.ActivityType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ActivityPointsDtoTest {

    @Test
    void shouldSetAndGetValuesCorrectly_forRequest() {
        UUID userId = UUID.randomUUID();

        ActivityPointsRequest request = new ActivityPointsRequest();
        request.setUserId(userId);
        request.setActivityType(ActivityType.CODE_REVIEW);

        assertEquals(userId, request.getUserId());
        assertEquals(ActivityType.CODE_REVIEW, request.getActivityType());
    }

    @Test
    void shouldSetAndGetValuesCorrectly_forResponse() {
        ActivityPointsResponse response = new ActivityPointsResponse();
        response.setPointsEarned(5);
        response.setActivityType(ActivityType.CODE_REVIEW);

        assertEquals(5, response.getPointsEarned());
        assertEquals(ActivityType.CODE_REVIEW, response.getActivityType());
    }

    @Test
    void shouldCreateObjectsUsingAllArgsConstructor() {
        UUID userId = UUID.randomUUID();

        ActivityPointsRequest request =
                new ActivityPointsRequest(userId, ActivityType.CODE_REVIEW);

        assertEquals(userId, request.getUserId());
        assertEquals(ActivityType.CODE_REVIEW, request.getActivityType());

        ActivityPointsResponse response =
                new ActivityPointsResponse(5, ActivityType.CODE_REVIEW);

        assertEquals(5, response.getPointsEarned());
        assertEquals(ActivityType.CODE_REVIEW, response.getActivityType());
    }
}
