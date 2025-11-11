package com.itachallenge.user.controller.userinteraction.favorite;

import com.itachallenge.user.controller.UserController;
import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.service.IUserSolutionService;
import com.itachallenge.userinteraction.service.favorite.FavoriteService;
import com.itachallenge.user.exception.UserGlobalExceptionHandler;
import com.itachallenge.user.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

class FavoriteControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private IUserSolutionService userSolutionService;

    @InjectMocks
    private UserController userController;

    private WebTestClient webTestClient;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(userController, new FavoriteController(favoriteService))
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
        UserDocument expectedUser = new UserDocument(UUID.randomUUID(), githubUsername, Role.ADMIN, null, null, 0);
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
    @DisplayName("GET /users/{userId}/favorites returns favorite challenges")
    void getUserFavorites_returnsFavorites() {
        UUID userId = UUID.randomUUID();
        Set<UUID> expectedFavorites = Set.of(UUID.randomUUID(), UUID.randomUUID());

        when(favoriteService.getUserFavorites(userId.toString())).thenReturn(Mono.just(expectedFavorites));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UUID.class)
                .hasSize(expectedFavorites.size())
                .contains(expectedFavorites.toArray(new UUID[0]));

        verify(favoriteService, times(1)).getUserFavorites(userId.toString());
    }

    @Test
    @DisplayName("GET /users/{userId}/favorites returns 404 if user not found")
    void getUserFavorites_returns404IfUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(favoriteService.getUserFavorites(userId.toString()))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("User not found");

        verify(favoriteService, times(1)).getUserFavorites(userId.toString());
    }

    @Test
    @DisplayName("GET /users/{userId}/favorites returns 400 if UUID is invalid")
    void getUserFavorites_returns400IfInvalidUUID() {
        String invalidUserId = "invalid-uuid";

        when(favoriteService.getUserFavorites(invalidUserId))
                .thenReturn(Mono.error(new BadUUIDException("The provided IDs are not valid.")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", invalidUserId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("The provided IDs are not valid.");

        verify(favoriteService, times(1)).getUserFavorites(invalidUserId);
    }
}
