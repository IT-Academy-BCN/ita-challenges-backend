package com.itachallenge.gamification.service;

import com.itachallenge.gamification.enums.ActivityType;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityTypeServiceImplTest {

    private final ActivityTypeServiceImpl activityTypeService = new ActivityTypeServiceImpl();

    @Test
    void getAvailableActivityTypes_returnsAllEnumNames() {
        StepVerifier.create(activityTypeService.getAvailableActivityTypes())
                .assertNext(response -> assertThat(response.getActivityTypes())
                        .containsExactly(Arrays.stream(ActivityType.values())
                                .map(Enum::name)
                                .toArray(String[]::new)))
                .verifyComplete();
    }
}
