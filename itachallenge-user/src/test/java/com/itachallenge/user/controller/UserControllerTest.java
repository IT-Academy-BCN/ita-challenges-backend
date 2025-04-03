package com.itachallenge.user.controller;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private WebTestClient webTestClient;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(userController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    void testEndpoint_ShouldReturnHelloMessage() {
        webTestClient.get()
                .uri("/itachallenge/api/v1/user/test")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello from ITA Challenge UserController!!!");
    }

    @Test
    void getUser_WhenUserExists_Returns200() {
        String githubUsername = "existingUser";
        UserDocument expectedUser = new UserDocument(UUID.randomUUID(), githubUsername, Role.ADMIN, null, null);
        when(userService.getUser(githubUsername)).thenReturn(Mono.just(expectedUser));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/" + githubUsername)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Validation-Status")
                .expectHeader().valueEquals("X-Validation-Status", "Success")
                .expectHeader().valueEquals("X-Github-Username", githubUsername)
                .expectBody(UserDocument.class).isEqualTo(expectedUser);

        verify(userService, times(1)).getUser(githubUsername);
    }

    @Test
    void getUser_WhenUserNotExists_Returns404() {
        String githubUsername = "nonExistentUser";
        when(userService.getUser(githubUsername)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/" + githubUsername)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectHeader().exists("X-Validation-Status")
                .expectHeader().valueEquals("X-Validation-Status", "Error")
                .expectHeader().valueEquals("X-Error-Message", "User not found")
                .expectBody().isEmpty();

        verify(userService, times(1)).getUser(githubUsername);
    }

    @Test
    void getUser_WhenServiceReturnsError_Returns500() {
        String githubUsername = "username";
        when(userService.getUser(any(String.class))).thenReturn(Mono.error(Exception::new));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/" + githubUsername)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectHeader().exists("X-Validation-Status")
                .expectHeader().valueEquals("X-Validation-Status", "Error")
                .expectHeader().valueEquals("X-Error-Message", "An error occurred retrieving user.")
                .expectBody().isEmpty();

        verify(userService, times(1)).getUser(githubUsername);
    }

    @Test
    void addToFavorites_WhenAdded_Returns201() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectHeader().valueEquals("X-Favorite-Added", "True")
                .expectHeader().valueEquals("X-Favorite-Message", "Challenge added to favorites.")
                .expectBody(Boolean.class).isEqualTo(true);

        verify(userService, times(1)).addChallengeToFavorites(userId, challengeId);
    }

    @Test
    void addToBookmarks_WhenAdded_Returns201() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToBookmarks(userId, challengeId))
                .thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectHeader().valueEquals("X-Bookmark-Added", "True")
                .expectHeader().valueEquals("X-Bookmark-Message", "Challenge added to Bookmarks.")
                .expectBody(Boolean.class).isEqualTo(true);

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
    }

    @Test
    void addToFavorites_WhenAlreadyInFavorites_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.just(false));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().valueEquals("X-Favorite-Added", "False")
                .expectHeader().valueEquals("X-Favorite-Message", "Challenge is already in favorites.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).addChallengeToFavorites(userId, challengeId);
    }

    @Test
    void addToBookmarks_WhenAlreadyInBookmarks_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToBookmarks(userId, challengeId))
                .thenReturn(Mono.just(false));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().valueEquals("X-Bookmark-Added", "False")
                .expectHeader().valueEquals("X-Bookmark-Message", "Challenge is already in Bookmarks.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
    }

    @Test
    void addToFavorites_WhenUserNotExists_Returns404() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectHeader().valueEquals("X-Favorite-Added", "False")
                .expectHeader().valueEquals("X-Favorite-Message", "User not found.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).addChallengeToFavorites(userId, challengeId);
    }

    @Test
    void addToBookmarks_WhenUserNotExists_Returns404() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectHeader().valueEquals("X-Bookmark-Added", "False")
                .expectHeader().valueEquals("X-Bookmark-Message", "User not found.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
    }

    @Test
    void addToFavorites_WhenBadFormattedId_Returns400() {
        String userId = "invalidUuid";
        String challengeId = "invalidUUid";
        when(userService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.error(new BadUUIDException("Error message")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectHeader().valueEquals("X-Favorite-Added", "False")
                .expectHeader().valueEquals("X-Favorite-Message", "The provided IDs are not valid.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).addChallengeToFavorites(userId, challengeId);
    }

    @Test
    void addToBookmarks_WhenBadFormattedId_Returns400() {
        String userId = "invalidUuid";
        String challengeId = "invalidUUid";
        when(userService.addChallengeToBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new BadUUIDException("Error message")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectHeader().valueEquals("X-Bookmark-Added", "False")
                .expectHeader().valueEquals("X-Bookmark-Message", "The provided IDs are not valid.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
    }

    @Test
    void addToFavorites_WhenUnexpectedError_Returns500() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.error(new Exception()));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectHeader().valueEquals("X-Favorite-Added", "False")
                .expectHeader().valueEquals("X-Favorite-Message", "Unexpected server error.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).addChallengeToFavorites(userId, challengeId);
    }

    @Test
    void addToBookmarks_WhenUnexpectedError_Returns500() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new Exception()));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectHeader().valueEquals("X-Bookmark-Added", "False")
                .expectHeader().valueEquals("X-Bookmark-Message", "Unexpected server error.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
    }

    @Test
    void deleteFromFavorites_WhenDeleted_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromFavorites(userId, challengeId))
                .thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().valueEquals("X-Favorite-Deleted", "True")
                .expectHeader().valueEquals("X-Favorite-Message", "Challenge deleted from favorites.")
                .expectBody(Boolean.class).isEqualTo(true);

        verify(userService, times(1)).deleteChallengeFromFavorites(userId, challengeId);
    }

    @Test
    void deleteFromFavorites_WhenNotInFavorites_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromFavorites(userId, challengeId))
                .thenReturn(Mono.just(false));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().valueEquals("X-Favorite-Deleted", "False")
                .expectHeader().valueEquals("X-Favorite-Message", "Challenge not found in user's favorites.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).deleteChallengeFromFavorites(userId, challengeId);
    }

    @Test
    void deleteFromFavorites_WhenUserNotExists_Returns404() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromFavorites(userId, challengeId))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectHeader().valueEquals("X-Favorite-Deleted", "False")
                .expectHeader().valueEquals("X-Favorite-Message", "User not found.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).deleteChallengeFromFavorites(userId, challengeId);
    }

    @Test
    void deleteFromFavorites_WhenBadFormattedId_Returns404() {
        String userId = "invalidUuid";
        String challengeId = "invalidUUid";
        when(userService.deleteChallengeFromFavorites(userId, challengeId))
                .thenReturn(Mono.error(new BadUUIDException("Error message")));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectHeader().valueEquals("X-Favorite-Deleted", "False")
                .expectHeader().valueEquals("X-Favorite-Message", "The provided IDs are not valid.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).deleteChallengeFromFavorites(userId, challengeId);
    }

    @Test
    void deleteFromFavorites_WhenUnexpectedError_Returns500() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromFavorites(userId, challengeId))
                .thenReturn(Mono.error(new Exception()));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectHeader().valueEquals("X-Favorite-Deleted", "False")
                .expectHeader().valueEquals("X-Favorite-Message", "Unexpected server error.")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).deleteChallengeFromFavorites(userId, challengeId);
    }

    @Test
    @DisplayName("GET /users/{userId}/favorites returns favorite challenges")
    void getUserFavorites_returnsFavorites() {
        UUID userId = UUID.randomUUID();
        Set<UUID> expectedFavorites = Set.of(UUID.randomUUID(), UUID.randomUUID());

        when(userService.getUserFavorites(userId.toString())).thenReturn(Mono.just(expectedFavorites));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", userId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Validation-Status", "Success")
                .expectBodyList(UUID.class)
                .hasSize(expectedFavorites.size())
                .contains(expectedFavorites.toArray(new UUID[0]));

        verify(userService, times(1)).getUserFavorites(userId.toString());
    }

    @Test
    @DisplayName("GET /users/{userId}/favorites returns 404 if user not found")
    void getUserFavorites_returns404IfUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userService.getUserFavorites(userId.toString()))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().valueEquals("X-Validation-Status", "Error")
                .expectHeader().valueEquals("X-Error-Message", "User not found")
                .expectBody().isEmpty();

        verify(userService, times(1)).getUserFavorites(userId.toString());
    }

    @Test
    @DisplayName("GET /users/{userId}/favorites returns 400 if UUID is invalid")
    void getUserFavorites_returns400IfInvalidUUID() {
        String invalidUserId = "invalid-uuid";

        when(userService.getUserFavorites(invalidUserId))
                .thenReturn(Mono.error(new BadUUIDException("Invalid UUID format")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", invalidUserId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectHeader().valueEquals("X-Validation-Status", "Error")
                .expectHeader().valueEquals("X-Error-Message", "Invalid UUID format")
                .expectBody().isEmpty();

        verify(userService, times(1)).getUserFavorites(invalidUserId);
    }

    @Test
    @DisplayName("GET /users/{userId}/favorites returns 500 if there is an internal error")
    void getUserFavorites_returns500IfUnexpectedError() {
        UUID userId = UUID.randomUUID();

        when(userService.getUserFavorites(userId.toString()))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", userId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectHeader().valueEquals("X-Validation-Status", "Error")
                .expectHeader().valueEquals("X-Error-Message", "Unexpected server error")
                .expectBody().isEmpty();

        verify(userService, times(1)).getUserFavorites(userId.toString());
    }

}
