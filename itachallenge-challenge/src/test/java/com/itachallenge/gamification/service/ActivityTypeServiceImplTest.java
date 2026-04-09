package com.itachallenge.gamification.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityTypeServiceImplTest {

    private final ActivityTypeServiceImpl activityTypeService = new ActivityTypeServiceImpl();

    @Test
    void getAvailableActivityTypes_returnsAllEnumNames() {
        StepVerifier.create(activityTypeService.getAvailableActivityTypes())
                .assertNext(response -> assertThat(response.getActivityTypes())
                        .containsExactly("CODE_REVIEW", "PRESENTATION", "CHALLENGE_COMPLETED"))
                .verifyComplete();
    }
}
