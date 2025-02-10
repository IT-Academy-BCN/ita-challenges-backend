package com.itachallenge.auth.service;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthServiceTest {

    private MockWebServer mockWebServer;
    private AuthService authService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("").toString();
        String githubTokenUri = baseUrl + "login/oauth/access_token";
        String githubUserInfoUri = baseUrl + "user";

        authService = new AuthService(WebClient.builder(), githubTokenUri, githubUserInfoUri, "test-client-id", "test-client-secret");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void exchangeCodeForToken_Successful() throws InterruptedException {
        String code = "auth-code";
        String accessToken = "github-access-token";
        String mockResponse = "{\"access_token\": \"" + accessToken + "\"}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        Mono<String> result = authService.exchangeCodeForToken(code);

        StepVerifier.create(result)
                .expectNext(accessToken)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/login/oauth/access_token", request.getRequestUrl().encodedPath());
        assertEquals("application/json", request.getHeader("Accept"));
    }

    @Test
    void validateTokenWithGithub_ValidToken_ReturnsUsername() throws Exception {
        String validToken = "valid-token";
        String githubUsername = "octocat";
        String mockResponse = "{\"login\": \"" + githubUsername + "\"}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        Mono<Map<String, Object>> result = authService.validateTokenWithGithub(validToken);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(true, response.get("isValid"));
                    assertEquals(githubUsername, response.get("username"));
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/user", request.getRequestUrl().encodedPath());
        assertEquals("token " + validToken, request.getHeader("Authorization"));
    }

}
