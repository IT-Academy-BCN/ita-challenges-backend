package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.ActivityPointsRequest;
import com.itachallenge.gamification.enums.ActivityType;
import com.itachallenge.gamification.service.UserScoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(ActivityController.class)
class ActivityControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserScoreService userScoreService;

    @Test
    void givenValidRequest_whenAssignPoints_thenReturnsPoints() {
        UUID userId = UUID.randomUUID();
        ActivityType activityType = ActivityType.CODE_REVIEW;

        when(userScoreService.assignPoints(userId, activityType))
                .thenReturn(Mono.just(activityType.getPoints()));

        ActivityPointsRequest request = new ActivityPointsRequest();
        request.setUserId(userId);
        request.setActivityType(activityType);

        webTestClient.post()
                .uri("/activities/points")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.pointsEarned").isEqualTo(activityType.getPoints())
                .jsonPath("$.activityType").isEqualTo(activityType.name());

        verify(userScoreService).assignPoints(userId, activityType);
    }

    @Test
    void givenNullUserId_whenAssignPoints_thenReturnsBadRequest() {
        ActivityPointsRequest request = new ActivityPointsRequest();
        request.setUserId(null);
        request.setActivityType(ActivityType.CODE_REVIEW);

        webTestClient.post()
                .uri("/activities/points")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void givenNullActivityType_whenAssignPoints_thenReturnsBadRequest() {
        ActivityPointsRequest request = new ActivityPointsRequest();
        request.setUserId(UUID.randomUUID());
        request.setActivityType(null);

        webTestClient.post()
                .uri("/activities/points")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void givenInvalidActivityType_whenAssignPoints_thenReturnsBadRequest() {
        String body = """
        {
          "userId": "%s",
          "activityType": "INVALID"
        }
        """.formatted(UUID.randomUUID());

        webTestClient.post()
                .uri("/activities/points")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON) // 👈 CLAVE
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void givenEmptyBody_whenAssignPoints_thenReturnsBadRequest() {
        webTestClient.post()
                .uri("/activities/points")
                .bodyValue(new ActivityPointsRequest())
                .exchange()
                .expectStatus().isBadRequest();
    }
}