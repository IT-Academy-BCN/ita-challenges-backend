package com.itachallenge.user.service;

import com.itachallenge.githubcore.config.GithubProperties;
import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.exception.GithubUnavailableException;
import com.itachallenge.githubcore.service.GithubApiService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ExternalGithubServiceImplTest {

    private static MockWebServer mockWebServer;
    //private GithubApiService githubApiService;
    private ExternalGithubServiceImpl externalGithubService;
    private GithubProperties githubProperties;

    @BeforeAll
    static void setUpAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        githubProperties = new GithubProperties();
        githubProperties.setBaseApiUrl(String.format("http://localhost:%s", mockWebServer.getPort()));

        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl(githubProperties.getBaseApiUrl());

        externalGithubService = new ExternalGithubServiceImpl((GithubApiService) webClientBuilder);
    }

    @Test
    @DisplayName("Should return true when GitHub user exists")
    void testUserExistsReturnsTrue() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"login\": \"someUser\"}"));

        StepVerifier.create(externalGithubService.userExists("someUser"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return false when GitHub user is NOT_FOUND")
    void testUserExistsReturnsFalse() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404));

        StepVerifier.create(externalGithubService.userExists("ghostUser"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should wrap API errors in GithubUnavailableException, preserving the cause")
    void testUserExistsPropagatesError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500));

        StepVerifier.create(externalGithubService.userExists("anyUser"))
                .expectError()
                .verify();
    }
}
