package com.itachallenge.user.controller.userinteraction.bookmark;

import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.userinteraction.service.bookmark.BookmarkService;
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

@WebFluxTest(controllers = {BookmarkController.class})
class BookmarkControllerTest {

    @MockBean
    private BookmarkService bookmarkService;

    @Autowired
    private WebTestClient webTestClient;

    @TestConfiguration
    static class TestConfig {

        @Bean (name="bookmarkWebClientBuilder")
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }

    @Test
    @DisplayName("GET /users/{userId}/bookmarks returns bookmarked challenges")
    void getUserBookmarks_NewPath_Success() {
        UUID userId = UUID.randomUUID();
        Set<UUID> expectedBookmarks = Set.of(UUID.randomUUID(), UUID.randomUUID());

        when(bookmarkService.getUserBookmarks(userId.toString()))
                .thenReturn(Mono.just(expectedBookmarks));

        webTestClient.get()
                .uri("/itachallenge/api/v1/users/{userId}/bookmarks", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UUID.class)
                .hasSize(expectedBookmarks.size())
                .contains(expectedBookmarks.toArray(new UUID[0]));

        verify(bookmarkService, times(1)).getUserBookmarks(userId.toString());
    }


    @Test
    @DisplayName("GET /users/{userId}/bookmarks returns 404 if user not found")
    void getUserBookmarks_returns404IfUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(bookmarkService.getUserBookmarks(userId.toString()))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/users/{userId}/bookmarks", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("User not found");

        verify(bookmarkService, times(1)).getUserBookmarks(userId.toString());
    }

    @Test
    @DisplayName("GET /users/{userId}/bookmarks returns 400 if UUID is invalid")
    void getUserBookmarks_returns400IfInvalidUUID() {
        String invalidUserId = "invalid-uuid";

        when(bookmarkService.getUserBookmarks(invalidUserId))
                .thenReturn(Mono.error(new BadUUIDException("The provided IDs are not valid.")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/users/{userId}/bookmarks", invalidUserId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("The provided IDs are not valid.");

        verify(bookmarkService, times(1)).getUserBookmarks(invalidUserId);
    }
    @Test
    @DisplayName("GET /users/{userId}/bookmarks returns 500 if there is an internal error")
    void getUserBookmarks_returns500IfUnexpectedError() {
        UUID userId = UUID.randomUUID();

        when(bookmarkService.getUserBookmarks(userId.toString()))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/users/{userId}/bookmarks", userId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class).isEqualTo("Unexpected error happened.");

        verify(bookmarkService, times(1)).getUserBookmarks(userId.toString());
    }
}