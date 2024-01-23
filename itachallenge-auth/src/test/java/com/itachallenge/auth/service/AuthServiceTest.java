package com.itachallenge.auth.service;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthServiceTest {

    private MockWebServer mockWebServer;
    private AuthService authService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/api/v1/tokens/validate").toString();
        authService = new AuthService(WebClient.builder().baseUrl(baseUrl));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    void validateWithSSO_Successful() throws InterruptedException {
        String responseBody = "{\"id\": \"some_id\"}";
        mockWebServer.enqueue(new MockResponse().setBody(responseBody));

        Mono<Boolean> result = authService.validateWithSSO("validToken");

        assertEquals(true, result.block());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/v1/tokens/validate", request.getPath());
        assertEquals("POST", request.getMethod());
        assertEquals("application/json", request.getHeader("Content-Type"));
        assertEquals("validToken", request.getBody().readUtf8());
    }

    @Test
    void validateWithSSO_Failure() throws InterruptedException {

        String responseBody = "{\"message\":\"Token is not valid\"}";
        mockWebServer.enqueue(new MockResponse().setBody(responseBody));

        Mono<Boolean> result = authService.validateWithSSO("invalidToken");

        assertEquals(false, result.block());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/v1/tokens/validate", request.getPath());
        assertEquals("POST", request.getMethod());
        assertEquals("application/json", request.getHeader("Content-Type"));
        assertEquals("invalidToken", request.getBody().readUtf8());
    }

}
