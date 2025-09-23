package com.itachallenge.githubcore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.githubcore.config.GithubCoreProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class GithubOAuthServiceImplTest {

    private MockWebServer mockWebServer;
    private GithubOAuthServiceImpl githubOAuthService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        GithubCoreProperties props = new GithubCoreProperties();
        props.setClientId("test-client-id");
        props.setClientSecret("test-client-secret");
        props.setTokenUri(mockWebServer.url("/login/oauth/access_token").toString());
        props.setUserInfoUri(mockWebServer.url("/user").toString());

        githubOAuthService = new GithubOAuthServiceImpl(
                WebClient.builder(),
                new ObjectMapper(),
                props
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void exchangeCodeForToken_ShouldReturnAccessToken_WhenResponseIsValid() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"mocked_token\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(githubOAuthService.exchangeCodeForToken("valid_code"))
                .expectNext("mocked_token")
                .verifyComplete();
    }

    @Test
    void exchangeCodeForToken_ShouldError_WhenResponseDoesNotContainAccessToken() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"error\":\"bad_verification_code\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(githubOAuthService.exchangeCodeForToken("invalid_code"))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void getUsernameFromToken_ShouldReturnUsername_WhenResponseIsValid() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"login\":\"mockuser\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(githubOAuthService.getUsernameFromToken("valid_token"))
                .expectNext("mockuser")
                .verifyComplete();
    }

    @Test
    void getUsernameFromToken_ShouldError_WhenResponseHasNoLogin() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"id\":12345}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(githubOAuthService.getUsernameFromToken("valid_token"))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void getUsernameFromToken_ShouldError_WhenResponseIsInvalidJson() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("invalid-json")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(githubOAuthService.getUsernameFromToken("valid_token"))
                .expectError(com.fasterxml.jackson.core.JsonProcessingException.class)
                .verify();
    }
}