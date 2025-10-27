package com.itachallenge.user.controller;

import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.user.exception.BadRequestException;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.service.IUserSolutionService;
import com.itachallenge.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = { UserController.class })
@ComponentScan(basePackages = {
        "com.itachallenge.errorcore",
        "com.itachallenge.user.exception"
})
@ActiveProfiles("test")
class UserControllerExceptionPathTest {

    @MockBean
    private UserService userService;
    @MockBean
    private IUserSolutionService userSolutionService;
    @MockBean
    private ErrorResponseBuilder responseBuilder;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getUser_WhenNotFound_Returns404() {
        String name = "missing";
        when(userService.getUser(name)).thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.get().uri("/itachallenge/api/v1/user/users/{name}", name)
                .exchange()
                .expectStatus().isNotFound();

        verify(userService).getUser(name);
    }

    @Test
    void getUser_WhenUnexpectedError_Returns500() {
        when(userService.getUser(any())).thenReturn(Mono.error(new RuntimeException("Boom")));

        webTestClient.get().uri("/itachallenge/api/v1/user/users/x")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void addToFavorites_InvalidUUID_Returns400() {
        when(userService.addChallengeToFavorites(any(), any()))
                .thenReturn(Mono.error(new BadUUIDException("Bad UUID")));

        webTestClient.post().uri("/itachallenge/api/v1/user/users/x/favorites/y")
                .exchange().expectStatus().isBadRequest();
    }

    @Test
    void addToFavorites_UserNotFound_Returns404() {
        when(userService.addChallengeToFavorites(any(), any()))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.post().uri("/itachallenge/api/v1/user/users/x/favorites/y")
                .exchange().expectStatus().isNotFound();
    }

    @Test
    void addToFavorites_Unexpected_Returns500() {
        when(userService.addChallengeToFavorites(any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Boom")));

        webTestClient.post().uri("/itachallenge/api/v1/user/users/x/favorites/y")
                .exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void addToBookmarks_InvalidUUID_Returns400() {
        when(userService.addChallengeToBookmarks(any(), any()))
                .thenReturn(Mono.error(new BadUUIDException("Bad UUID")));

        webTestClient.post().uri("/itachallenge/api/v1/user/users/x/bookmarks/y")
                .exchange().expectStatus().isBadRequest();
    }

    @Test
    void addToBookmarks_UserNotFound_Returns404() {
        when(userService.addChallengeToBookmarks(any(), any()))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.post().uri("/itachallenge/api/v1/user/users/x/bookmarks/y")
                .exchange().expectStatus().isNotFound();
    }

    @Test
    void addToBookmarks_Unexpected_Returns500() {
        when(userService.addChallengeToBookmarks(any(), any()))
                .thenReturn(Mono.error(new Exception("Err")));

        webTestClient.post().uri("/itachallenge/api/v1/user/users/x/bookmarks/y")
                .exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void deleteFavorites_UserNotFound_Returns404() {
        when(userService.deleteChallengeFromFavorites(any(), any()))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.delete().uri("/itachallenge/api/v1/user/users/x/favorites/y")
                .exchange().expectStatus().isNotFound();
    }

    @Test
    void deleteFavorites_InvalidUUID_Returns400() {
        when(userService.deleteChallengeFromFavorites(any(), any()))
                .thenReturn(Mono.error(new BadUUIDException("Invalid ID")));

        webTestClient.delete().uri("/itachallenge/api/v1/user/users/x/favorites/y")
                .exchange().expectStatus().isBadRequest();
    }

    @Test
    void deleteFavorites_Unexpected_Returns500() {
        when(userService.deleteChallengeFromFavorites(any(), any()))
                .thenReturn(Mono.error(new Exception("Boom")));

        webTestClient.delete().uri("/itachallenge/api/v1/user/users/x/favorites/y")
                .exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void deleteBookmarks_UserNotFound_Returns404() {
        when(userService.deleteChallengeFromBookmarks(any(), any()))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.delete().uri("/itachallenge/api/v1/user/users/x/bookmarks/y")
                .exchange().expectStatus().isNotFound();
    }

    @Test
    void deleteBookmarks_InvalidUUID_Returns400() {
        when(userService.deleteChallengeFromBookmarks(any(), any()))
                .thenReturn(Mono.error(new BadUUIDException("Invalid ID")));

        webTestClient.delete().uri("/itachallenge/api/v1/user/users/x/bookmarks/y")
                .exchange().expectStatus().isBadRequest();
    }

    @Test
    void deleteBookmarks_Unexpected_Returns500() {
        when(userService.deleteChallengeFromBookmarks(any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Err")));

        webTestClient.delete().uri("/itachallenge/api/v1/user/users/x/bookmarks/y")
                .exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getFavorites_InvalidUUID_Returns400() {
        when(userService.getUserFavorites(any()))
                .thenReturn(Mono.error(new BadUUIDException("Invalid")));

        webTestClient.get().uri("/itachallenge/api/v1/user/users/x/favorites")
                .exchange().expectStatus().isBadRequest();
    }

    @Test
    void getFavorites_UserNotFound_Returns404() {
        when(userService.getUserFavorites(any()))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.get().uri("/itachallenge/api/v1/user/users/x/favorites")
                .exchange().expectStatus().isNotFound();
    }

    @Test
    void getFavorites_Unexpected_Returns500() {
        when(userService.getUserFavorites(any()))
                .thenReturn(Mono.error(new RuntimeException("Boom")));

        webTestClient.get().uri("/itachallenge/api/v1/user/users/x/favorites")
                .exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getBookmarks_InvalidUUID_Returns400() {
        when(userService.getUserBookmarks(any()))
                .thenReturn(Mono.error(new BadUUIDException("Bad")));

        webTestClient.get().uri("/itachallenge/api/v1/user/users/x/bookmarks")
                .exchange().expectStatus().isBadRequest();
    }

    @Test
    void getBookmarks_UserNotFound_Returns404() {
        when(userService.getUserBookmarks(any()))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.get().uri("/itachallenge/api/v1/user/users/x/bookmarks")
                .exchange().expectStatus().isNotFound();
    }

    @Test
    void getBookmarks_Unexpected_Returns500() {
        when(userService.getUserBookmarks(any()))
                .thenReturn(Mono.error(new RuntimeException("Boom")));

        webTestClient.get().uri("/itachallenge/api/v1/user/users/x/bookmarks")
                .exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getSolutions_InvalidUUID_Returns400() {
        String badUserId = "not-a-uuid";
        when(userSolutionService.getAllSolutionsByUser(any()))
                .thenReturn(Flux.error(new BadRequestException("The 'userId' parameter cannot be null or empty.")));

        webTestClient.get().uri("/itachallenge/api/v1/user/users/{userId}/solutions",badUserId)
                .exchange().expectStatus().isBadRequest();
    }

    @Test
    void getSolutions_NotFound_Returns404() {
        when(userSolutionService.getAllSolutionsByUser(any()))
                .thenReturn(Flux.error(new NotFoundException("Solutions not found")));

        webTestClient.get().uri("/itachallenge/api/v1/user/users/x/solutions")
                .exchange().expectStatus().isNotFound();
    }

    @Test
    void getSolutions_Unexpected_Returns500() {
        when(userSolutionService.getAllSolutionsByUser(any()))
                .thenReturn(Flux.error(new RuntimeException("Boom")));

        webTestClient.get().uri("/itachallenge/api/v1/user/users/x/solutions")
                .exchange().expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}