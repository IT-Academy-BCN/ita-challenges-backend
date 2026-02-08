package com.itachallenge.user.controller;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.exception.UserGlobalExceptionHandler;
import com.itachallenge.user.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

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
        webTestClient = WebTestClient.bindToController(userController)
                .controllerAdvice(new UserGlobalExceptionHandler())
                .build();
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
        UserDocument expectedUser = new UserDocument(UUID.randomUUID(), githubUsername, Role.ADMIN, 0);
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
        when(userService.getUser(githubUsername)).thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/" + githubUsername)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("User not found");

        verify(userService, times(1)).getUser(githubUsername);
    }

    @Test
    void getUser_WhenServiceReturnsError_Returns500() {
        String githubUsername = "username";
        when(userService.getUser(any(String.class))).thenReturn(Mono.error( new RuntimeException()));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/" + githubUsername)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class).isEqualTo("Unexpected error happened.");

        verify(userService, times(1)).getUser(githubUsername);
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
                .expectBody(Boolean.class).isEqualTo(true);

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
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
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
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
                .expectBody(String.class).isEqualTo("User not found");

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
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
                .expectBody(String.class).isEqualTo("The provided IDs are not valid.");

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
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
                .expectBody(String.class).isEqualTo("Unexpected error happened.");

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
    }

    @Test
    void deleteFromBookmarks_WhenDeleted_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromBookmarks(userId, challengeId))
                .thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(Boolean.class).isEqualTo(true);

        verify(userService, times(1)).deleteChallengeFromBookmarks(userId, challengeId);
    }

    @Test
    void deleteFromBookmarks_WhenNotInBookmarks_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromBookmarks(userId, challengeId))
                .thenReturn(Mono.just(false));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).deleteChallengeFromBookmarks(userId, challengeId);
    }

    @Test
    void deleteFromBookmarks_WhenUserNotExists_Returns404() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(String.class).isEqualTo("User not found");

        verify(userService, times(1)).deleteChallengeFromBookmarks(userId, challengeId);
    }

    @Test
    void deleteFromBookmarks_WhenBadFormattedId_Returns404() {
        String userId = "invalidUuid";
        String challengeId = "invalidUUid";
        when(userService.deleteChallengeFromBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new BadUUIDException("Error message")));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(String.class).isEqualTo("The provided IDs are not valid.");

        verify(userService, times(1)).deleteChallengeFromBookmarks(userId, challengeId);
    }

    @Test
    void deleteFromBookmarks_WhenUnexpectedError_Returns500() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new Exception()));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class).isEqualTo("Unexpected error happened.");

        verify(userService, times(1)).deleteChallengeFromBookmarks(userId, challengeId);
    }

}