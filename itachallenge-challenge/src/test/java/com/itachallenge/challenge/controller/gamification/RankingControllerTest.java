package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.RankingResponseDto;
import com.itachallenge.common.exception.GlobalExceptionHandler;
import com.itachallenge.gamification.service.UserScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = RankingController.class)
@ExtendWith(SpringExtension.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RankingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserScoreService pointsService;

    private RankingResponseDto rankingResponseDto1;
    private RankingResponseDto rankingResponseDto2;

    private static final String RANKING_URL = "/itachallenge/api/v1/users/ranking";

    @BeforeEach
    void setUp() {
        rankingResponseDto1 = new RankingResponseDto("user1", 100);
        rankingResponseDto2 = new RankingResponseDto("user2", 50);
    }

    @Test
    @DisplayName("GET /ranking - should return users ordered by points descending")
    void getRanking_ReturnsUsersInDescendingOrder() {
        when(pointsService.getRankingDescOrder())
                .thenReturn(Flux.just(rankingResponseDto1 ,rankingResponseDto2)); // 50 before 100

        webTestClient.get()
                .uri(RANKING_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].username").isEqualTo("user1")
                .jsonPath("$[0].points").isEqualTo(100)
                .jsonPath("$[1].username").isEqualTo("user2")
                .jsonPath("$[1].points").isEqualTo(50);

        verify(pointsService, times(1)).getRankingDescOrder();
    }

    @Test
    @DisplayName("GET /ranking - should return empty list when no rankings exist")
    void getRanking_NoRankings_ReturnsEmptyList() {
        when(pointsService.getRankingDescOrder()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri(RANKING_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RankingResponseDto.class)
                .hasSize(0);

        verify(pointsService, times(1)).getRankingDescOrder();
    }
}