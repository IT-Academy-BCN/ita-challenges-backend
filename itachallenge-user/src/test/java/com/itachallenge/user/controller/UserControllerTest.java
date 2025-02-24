package com.itachallenge.user.controller;

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
        when(userService.isMentor(any(Mono.class))).thenReturn(Mono.just(true));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("Mentor validation successful")
                .expectBody(Boolean.class).isEqualTo(true);

        verify(userService, times(1)).isMentor(any(Mono.class));
    }

    @Test
    void validateMentorExists_WhenUserIsNotMentor_Returns403() {
        String githubUsername = "invalidMentor";
        when(userService.isMentor(any(Mono.class))).thenReturn(Mono.just(false));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().exists("Validation unsuccessful")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).isMentor(any(Mono.class));
    }

    @Test
    void validateMentorExists_WhenErrorOccurs_Returns400() {
        String githubUsername = "errorUser";
        when(userService.isMentor(any(Mono.class))).thenReturn(Mono.error(new RuntimeException("Database error")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().exists("Validation error")
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).isMentor(any(Mono.class));
    }

    @Test
    void validateMentorExists_WhenInternalServerErrorOccurs_Returns500() {
        String githubUsername = "internalErrorUser";
        when(userService.isMentor(any(Mono.class))).thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userService, times(1)).isMentor(any(Mono.class));
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
        when(userService.isMentor(any(Mono.class))).thenReturn(Mono.just(false));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().exists("Validation unsuccessful")
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void validateMentorExists_WhenGithubUsernameCaseMismatch_Returns403() {
        when(userService.isMentor(any(Mono.class))).thenReturn(Mono.just(false));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", "validMentor")
                        .build())
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().exists("Validation unsuccessful")
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void validateMentorExists_WhenIllegalArgumentExceptionOccurs_Returns400() {
        String githubUsername = "invalidUser";
        when(userService.isMentor(any(Mono.class))).thenReturn(Mono.error(new IllegalArgumentException("Invalid input")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/itachallenge/api/v1/user/validate-mentor-exists")
                        .queryParam("githubUsername", githubUsername)
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().exists("Validation error")
                .expectBody(Boolean.class).isEqualTo(false);
    }

}
