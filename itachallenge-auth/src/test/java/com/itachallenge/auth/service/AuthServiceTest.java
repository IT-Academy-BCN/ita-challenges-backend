package com.itachallenge.auth.service;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
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

        String baseUrl = mockWebServer.url("/user").toString();
        authService = new AuthService(WebClient.builder().baseUrl(baseUrl));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
//
//    @Test
//    void exchangeCodeForToken_Successful() throws InterruptedException {
//        String code = "auth-code";
//        String accessToken = "github-access-token";
//        String mockResponse = "{\"access_token\": \"" + accessToken + "\"}";
////
//        mockWebServer.enqueue(new MockResponse()
//                .setBody(mockResponse)
//                .setResponseCode(200)
//                .addHeader("Content-Type", "application/json"));
//
//        Mono<String> result = authService.exchangeCodeForToken(code);
//
//        StepVerifier.create(result)
//                .expectNext(accessToken)
//                .verifyComplete();
//
//        RecordedRequest request = mockWebServer.takeRequest();
//        assertEquals("/login/oauth/access_token", request.getPath());
//        assertEquals("application/json", request.getHeader("Accept"));
//    }

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
        assertEquals("/user", request.getPath());
        assertEquals("token " + validToken, request.getHeader("Authorization"));
    }


    // Old tests
//    @Test
//    void validateWithSSO_Successful() throws InterruptedException {
//        String responseBody = "{\"id\": \"some_id\"}";
//        mockWebServer.enqueue(new MockResponse().setBody(responseBody));
//
//        Mono<Boolean> result = authService.validateWithSSO("validToken");
//
//        assertEquals(true, result.block());
//
//        RecordedRequest request = mockWebServer.takeRequest();
//        assertEquals("/api/v1/tokens/validate", request.getPath());
//        assertEquals("POST", request.getMethod());
//        assertEquals("application/json", request.getHeader("Content-Type"));
//        assertEquals("validToken", request.getBody().readUtf8());
//    }
//
//    @Test
//    void validateWithSSO_Failure() throws InterruptedException {
//
//        String responseBody = "{\"message\":\"Token is not valid\"}";
//        mockWebServer.enqueue(new MockResponse().setBody(responseBody));
//
//        Mono<Boolean> result = authService.validateWithSSO("invalidToken");
//
//        assertEquals(false, result.block());
//
//        RecordedRequest request = mockWebServer.takeRequest();
//        assertEquals("/api/v1/tokens/validate", request.getPath());
//        assertEquals("POST", request.getMethod());
//        assertEquals("application/json", request.getHeader("Content-Type"));
//        assertEquals("invalidToken", request.getBody().readUtf8());
//    }
//
//    @Test
//    void validateWithSSO_Unexpected_Response() throws InterruptedException {
//
//        String responseBody = "Invalid JSON format";
//        mockWebServer.enqueue(new MockResponse().setBody(responseBody));
//
//        Mono<Boolean> result = authService.validateWithSSO("someToken");
//
//        StepVerifier.create(result)
//                .expectNext(false)
//                .verifyComplete();
//    }


}
