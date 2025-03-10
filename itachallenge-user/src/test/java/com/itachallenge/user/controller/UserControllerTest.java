package com.itachallenge.user.controller;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void validateMentorExists_WhenUserIsMentor_Returns200() {
        String githubUsername = "validMentor";
        when(userService.exists(any(Mono.class))).thenReturn(Mono.just(true));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Validation-Status")
                .expectHeader().valueEquals("X-Validation-Status", "Success")
                .expectHeader().valueEquals("X-Github-Username", githubUsername)
                .expectBody(Boolean.class).isEqualTo(true);

        verify(userService, times(1)).exists(any(Mono.class));
    }

    @Test
    void validateMentorExists_WhenUserIsNotMentor_Returns403() {
        String githubUsername = "invalidMentor";
        when(userService.exists(any(Mono.class))).thenReturn(Mono.just(false));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().exists("X-Validation-Status")
                .expectHeader().valueEquals("X-Validation-Status", "Failed")
                .expectHeader().valueEquals("X-Github-Username", githubUsername)
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).exists(any(Mono.class));
    }

    @Test
    void validateMentorExists_WhenErrorOccurs_Returns400() {
        String githubUsername = "errorUser";
        when(userService.exists(any(Mono.class))).thenReturn(Mono.error(new RuntimeException("Database error")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().exists("X-Validation-Status")
                .expectHeader().valueEquals("X-Validation-Status", "Error")
                .expectHeader().exists("X-Error-Message")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).exists(any(Mono.class));
    }

    @Test
    void validateMentorExists_WhenInternalServerErrorOccurs_Returns500() {
        String githubUsername = "internalErrorUser";
        when(userService.exists(any(Mono.class))).thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userService, times(1)).exists(any(Mono.class));
    }

    @Test
    void validateMentorExists_WhenGithubUsernameMissing_Returns400() {
        webTestClient.get()
                .uri("/itachallenge/api/v1/user/validate-mentor-exists")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void validateMentorExists_WhenGithubUsernameHasSpaces_Returns403() {
        String githubUsername = " mentorWithSpace ";
        when(userService.exists(any(Mono.class))).thenReturn(Mono.just(false));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().exists("X-Validation-Status")
                .expectHeader().valueEquals("X-Validation-Status", "Failed")
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void validateMentorExists_WhenGithubUsernameCaseMismatch_Returns403() {
        when(userService.exists(any(Mono.class))).thenReturn(Mono.just(false));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", "validMentor")
                        .build())
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().exists("X-Validation-Status")
                .expectHeader().valueEquals("X-Validation-Status", "Failed")
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void validateMentorExists_WhenIllegalArgumentExceptionOccurs_Returns400() {
        String githubUsername = "invalidUser";
        when(userService.exists(any(Mono.class))).thenReturn(Mono.error(new IllegalArgumentException("Invalid input")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().exists("X-Validation-Status")
                .expectHeader().valueEquals("X-Validation-Status", "Error")
                .expectHeader().exists("X-Error-Message")
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void getUser_WhenUserExists_Returns200() {
        String githubUsername = "existingUser";
        UserDocument expectedUser = new UserDocument(UUID.randomUUID(), githubUsername, Role.ADMIN);
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

}
