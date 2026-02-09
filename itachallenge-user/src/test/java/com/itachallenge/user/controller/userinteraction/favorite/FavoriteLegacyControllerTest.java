package com.itachallenge.user.controller.userinteraction.favorite;

import com.itachallenge.userinteraction.service.favorite.FavoriteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = FavoriteLegacyController.class)
class FavoriteLegacyControllerTest {

    @MockBean
    private FavoriteService favoriteService;

    @Autowired
    private WebTestClient webTestClient;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }

    @Test
    void addToFavoritesLegacy_Returns201AndHeaders() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(favoriteService.addChallengeToFavorites(userId, challengeId)).thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/itachallenge/api/v1/userinteraction/favorites/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectHeader().valueEquals("Deprecation", "true")
                .expectHeader().exists("Link");
    }

    @Test
    void getUserFavoritesLegacy_Returns200AndHeaders() {
        String userId = UUID.randomUUID().toString();
        when(favoriteService.getUserFavorites(userId)).thenReturn(Mono.just(Set.of()));

        webTestClient.get()
                .uri("/itachallenge/api/v1/userinteraction/favorites/" + userId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Deprecation", "true");
    }

    @Test
    void deleteFromFavoritesLegacy_Returns200AndHeaders() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(favoriteService.deleteChallengeFromFavorites(userId, challengeId)).thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/userinteraction/favorites/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Deprecation", "true");
    }
}