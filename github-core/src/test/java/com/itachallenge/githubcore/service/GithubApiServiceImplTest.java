package com.itachallenge.githubcore.service;

import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.exception.GithubUnavailableException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class GithubApiServiceImplTest {

    private static MockWebServer mockWebServer;
    private GithubApiServiceImpl githubApiServiceImpl;

    @BeforeAll
    static void startServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

    }

    @AfterAll
    static void shutdownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setup() {
        String mockBaseUrl = mockWebServer.url("/").toString();
        githubApiServiceImpl = new GithubApiServiceImpl(WebClient.builder(), mockBaseUrl);
    }

    @Test
    void exists_ShouldReturnTrue_WhenResponseIs200() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"login\":\"testUser\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(githubApiServiceImpl.userExists("testUser"))
                .expectNext(GithubUserStatus.FOUND)
                .verifyComplete();
    }

    @Test
    void exists_ShouldReturnFalse_WhenResponseIs404() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"message\":\"Not Found\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(githubApiServiceImpl.userExists("testuser19"))
                .expectNext(GithubUserStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void exists_ShouldReturnFalse_WhenResponseIs500() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"message\":\"Server Error\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(githubApiServiceImpl.userExists("unknownuser"))
                .expectError(GithubUnavailableException.class)
                .verify();
    }


}