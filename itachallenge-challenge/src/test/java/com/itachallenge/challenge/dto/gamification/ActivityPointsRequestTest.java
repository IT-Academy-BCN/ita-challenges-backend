package com.itachallenge.challenge.dto.gamification;

import com.itachallenge.gamification.enums.ActivityType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ActivityPointsRequestTest {

    @Test
    void shouldTestAllMethods() {
        UUID userId = UUID.randomUUID();

        ActivityPointsRequest request = new ActivityPointsRequest();
        request.setUserId(userId);
        request.setActivityType(ActivityType.CODE_REVIEW);

        assertEquals(userId, request.getUserId());
        assertEquals(ActivityType.CODE_REVIEW, request.getActivityType());

        ActivityPointsRequest request2 =
                new ActivityPointsRequest(userId, ActivityType.CODE_REVIEW);

        assertEquals(request, request2);
        assertEquals(request.hashCode(), request2.hashCode());

        assertNotNull(request.toString());
    }
}