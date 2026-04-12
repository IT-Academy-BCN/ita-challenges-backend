package com.itachallenge.challenge.dto.gamification;

import com.itachallenge.gamification.enums.ActivityType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActivityPointsResponseTest {

    @Test
    void shouldTestAllMethods() {
        ActivityPointsResponse response = new ActivityPointsResponse();
        response.setPointsEarned(5);
        response.setActivityType(ActivityType.CODE_REVIEW);

        assertEquals(5, response.getPointsEarned());
        assertEquals(ActivityType.CODE_REVIEW, response.getActivityType());

        ActivityPointsResponse response2 =
                new ActivityPointsResponse(5, ActivityType.CODE_REVIEW);

        assertEquals(response, response2);
        assertEquals(response.hashCode(), response2.hashCode());

        assertNotNull(response.toString());
    }
}