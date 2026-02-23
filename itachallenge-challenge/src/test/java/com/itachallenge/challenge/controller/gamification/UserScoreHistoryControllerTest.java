package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.PointEntryDto;
import com.itachallenge.challenge.dto.gamification.PointsHistoryDto;
import com.itachallenge.challenge.service.IChallengeJwtFacade;
import com.itachallenge.gamification.service.PointsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = UserScoreHistoryController.class)
class UserScoreHistoryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PointsService pointsService;

    @MockBean
    private IChallengeJwtFacade jwtFacade;

    private final String VALID_TOKEN = "Bearer valid.token";
    private final UUID VALID_USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        when(jwtFacade.getUserUuIdFromAuthenticationHeader(anyString())).thenReturn(VALID_USER_ID.toString());
    }

    @Test
    void getUserPointsHistory_whenUserHasPoints_thenReturns200AndHistory() {
        PointsHistoryDto expectedResponse = PointsHistoryDto.builder()
                .totalPoints(15)
                .history(List.of(
                        PointEntryDto.builder().points(10).createdAt("2026-02-23T11:11:11").build(),
                        PointEntryDto.builder().points(5).createdAt("2026-01-23T11:11:11").build()
                ))
                .build();

        when(jwtFacade.getUserUuIdFromAuthenticationHeader(VALID_TOKEN)).thenReturn(VALID_USER_ID.toString());
        when(pointsService.getUserPointsHistory(VALID_USER_ID)).thenReturn(Mono.just(expectedResponse));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/itachallenge/api/v1/me/points/history")
                .header("Authorization", VALID_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        response.expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.totalPoints").isEqualTo(15)
                .jsonPath("$.history.length()").isEqualTo(2)
                .jsonPath("$.history[0].points").isEqualTo(10)
                .jsonPath("$.history[0].date").exists();
    }

    @Test
    void getUserPointsHistory_WhenUserHasNoPoints_Returns200AndEmptyList() {
        PointsHistoryDto emptyResponse = PointsHistoryDto.builder()
                .totalPoints(0)
                .history(List.of())
                .build();

        when(jwtFacade.getUserUuIdFromAuthenticationHeader(VALID_TOKEN)).thenReturn(VALID_USER_ID.toString());
        when(pointsService.getUserPointsHistory(VALID_USER_ID)).thenReturn(Mono.just(emptyResponse));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/itachallenge/api/v1/me/points/history")
                .header("Authorization", VALID_TOKEN)
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalPoints").isEqualTo(0)
                .jsonPath("$.history").isEmpty();
    }

    @Test
    void getUserPointsHistory_WhenNoTokenProvided_Returns400BadRequest() {

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/itachallenge/api/v1/me/points/history")
                .exchange();

        response.expectStatus().isBadRequest();
    }
}
