package com.itachallenge.auth.service;


import com.itachallenge.auth.dto.User;
import com.itachallenge.auth.exception.CustomBadRequestException;
import com.itachallenge.auth.exception.CustomInternalServerErrorException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

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

    @Test
    void fetchUserData_UserExists_ReturnsUser() throws InterruptedException, JsonProcessingException {
        String githubUsername = "octocat";
        User expectedUser = new User("1234", githubUsername, "ADMIN");

        ObjectMapper objectMapper = new ObjectMapper();
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(expectedUser))
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        Mono<User> result = userService.fetchUserData(githubUsername);

        StepVerifier.create(result)
                .expectNext(expectedUser)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/itachallenge/api/v1/user/users/" + githubUsername, request.getRequestUrl().encodedPath());
    }

    @Test
    void fetchUserData_NotFound_ReturnsEmptyMono() throws InterruptedException {
        String githubUsername = "octocat";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json"));

        Mono<User> result = userService.fetchUserData(githubUsername);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/itachallenge/api/v1/user/users/" + githubUsername, request.getRequestUrl().encodedPath());
    }

    @Test
    void fetchUserData_BadRequest_ThrowsCustomBadRequestException() throws InterruptedException {
        String githubUsername = "octocat";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json"));

        Mono<User> result = userService.fetchUserData(githubUsername);

        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/itachallenge/api/v1/user/users/" + githubUsername, request.getRequestUrl().encodedPath());
    }

    @Test
    void fetchUserData_500_ThrowsInternalServerErrorException() throws InterruptedException {
        String githubUsername = "octocat";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", "application/json"));

        Mono<User> result = userService.fetchUserData(githubUsername);

        StepVerifier.create(result)
                .expectError(CustomInternalServerErrorException.class)
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/itachallenge/api/v1/user/users/" + githubUsername, request.getRequestUrl().encodedPath());
    }

    @Test
    void callUserTest_Ok_ReturnsSalute() throws InterruptedException {
        String expectedResponse = "Hello from User Micro-Service";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(expectedResponse)
                .addHeader("Content-Type", "application/json"));

        Mono<String> result = userService.callUserTest();

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/itachallenge/api/v1/user/test", request.getRequestUrl().encodedPath());
    }

    @Test
    void callUserTest_Error_ReturnsErrorMessage() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", "application/json"));

        Mono<String> result = userService.callUserTest();

        StepVerifier.create(result)
                .expectNext("Error calling User microservice")
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/itachallenge/api/v1/user/test", request.getRequestUrl().encodedPath());
    }

}
