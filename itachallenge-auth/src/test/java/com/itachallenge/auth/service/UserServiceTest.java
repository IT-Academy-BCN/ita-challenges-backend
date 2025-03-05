package com.itachallenge.auth.service;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {

    private MockWebServer mockWebServer;
    private UserService userService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("").toString();
        String userServiceUrl = baseUrl;

        userService = new UserService(
                WebClient.builder(),
                userServiceUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    /*
    @Test
    void validateUserExists_UserExists_ReturnsTrue() throws InterruptedException {
        String githubUsername = "octocat";
        mockWebServer.enqueue(new MockResponse()
                .setBody("true")
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.validateUserExists(githubUsername);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/itachallenge/api/v1/user/validate-mentor-exists", request.getRequestUrl().encodedPath());
        assertEquals(githubUsername, request.getRequestUrl().queryParameter("githubUsername"));
    }

    @Test
    void validateUserExists_UserDoesNotExist_ReturnsFalse() {
        String githubUsername = "unknown-user";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404));

        Mono<Boolean> result = userService.validateUserExists(githubUsername);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void validateUserExists_ServiceError_ReturnsFalse() {
        String githubUsername = "octocat";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500));

        Mono<Boolean> result = userService.validateUserExists(githubUsername);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

     */

}
