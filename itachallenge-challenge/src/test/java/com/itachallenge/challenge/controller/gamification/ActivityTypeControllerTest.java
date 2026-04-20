package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.ActivityTypeResponseDto;
import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.gamification.service.ActivityTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(ActivityTypeController.class)
class ActivityTypeControllerTest {

    private static final String URL = "/itachallenge/api/v1/activity-types";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ActivityTypeService activityTypeService;

    @Test
    void getAvailableActivityTypes_returnsOkAndActivityTypes() {
        ActivityTypeResponseDto responseDto = ActivityTypeResponseDto.builder()
                .activityTypes(List.of("CODE_REVIEW", "PRESENTATION", "CHALLENGE_COMPLETED"))
                .build();

        when(activityTypeService.getAvailableActivityTypes()).thenReturn(Mono.just(responseDto));

        webTestClient.get()
                .uri(URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.activityTypes.length()").isEqualTo(3)
                .jsonPath("$.activityTypes[0]").isEqualTo("CODE_REVIEW")
                .jsonPath("$.activityTypes[1]").isEqualTo("PRESENTATION")
                .jsonPath("$.activityTypes[2]").isEqualTo("CHALLENGE_COMPLETED");

        verify(activityTypeService).getAvailableActivityTypes();
    }

    @Test
    void getAvailableActivityTypes_whenEmpty_returnsOkAndEmptyList() {
        ActivityTypeResponseDto emptyResponse = ActivityTypeResponseDto.builder()
                .activityTypes(Collections.emptyList())
                .build();

        when(activityTypeService.getAvailableActivityTypes()).thenReturn(Mono.just(emptyResponse));

        webTestClient.get()
                .uri(URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.activityTypes").isEmpty();

        verify(activityTypeService).getAvailableActivityTypes();
    }

    @Test
    void getAvailableActivityTypes_whenServiceError_returns500Error() {
        when(activityTypeService.getAvailableActivityTypes())
                .thenReturn(Mono.error(new InternalServerErrorException("Unexpected error")));

        webTestClient.get()
                .uri(URL)
                .exchange()
                .expectStatus().is5xxServerError();

        verify(activityTypeService).getAvailableActivityTypes();
    }
}
