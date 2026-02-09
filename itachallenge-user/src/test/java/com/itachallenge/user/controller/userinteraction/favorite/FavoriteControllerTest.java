package com.itachallenge.user.controller.userinteraction.favorite;

import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.userinteraction.service.favorite.FavoriteService;
import org.junit.jupiter.api.DisplayName;
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

@WebFluxTest(controllers = FavoriteController.class)
class FavoriteControllerTest {

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
    void addToFavorites_WhenAdded_Returns201() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(favoriteService.addChallengeToFavorites(userId, challengeId)).thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/itachallenge/api/v1/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void addToFavorites_WhenAlreadyInFavorites_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(favoriteService.addChallengeToFavorites(userId, challengeId)).thenReturn(Mono.just(false));

        webTestClient.post()
                .uri("/itachallenge/api/v1/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void addToFavorites_WhenUserNotExists_Returns404() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(favoriteService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("User not found");
    }

    @Test
    void addToFavorites_WhenBadFormattedId_Returns400() {
        String userId = "invalidUuid";
        String challengeId = "invalidUuid";
        when(favoriteService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.error(new BadUUIDException("The provided IDs are not valid.")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("The provided IDs are not valid.");
    }

    @Test
    @DisplayName("GET /users/{userId}/favorites returns favorite challenges")
    void getUserFavorites_returnsFavorites() {
        UUID userId = UUID.randomUUID();
        Set<UUID> expectedFavorites = Set.of(UUID.randomUUID(), UUID.randomUUID());
        when(favoriteService.getUserFavorites(userId.toString())).thenReturn(Mono.just(expectedFavorites));

        webTestClient.get()
                .uri("/itachallenge/api/v1/users/{userId}/favorites", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UUID.class)
                .hasSize(expectedFavorites.size());
    }

    @Test
    void deleteFromFavorites_WhenDeleted_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(favoriteService.deleteChallengeFromFavorites(userId, challengeId)).thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void deleteFromFavorites_WhenUnexpectedError_Returns500() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(favoriteService.deleteChallengeFromFavorites(userId, challengeId)).thenReturn(Mono.error(new Exception()));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class).isEqualTo("Unexpected error happened.");
    }
}