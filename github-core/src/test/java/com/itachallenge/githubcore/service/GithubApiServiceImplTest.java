package com.itachallenge.githubcore.service;

import com.itachallenge.githubcore.config.GithubProperties;
import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.dto.GithubUserRequestDto;
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
    private GithubProperties githubProperties;

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
        String baseUrl = mockWebServer.url("/").toString();

        githubProperties = new GithubProperties();
        githubProperties.setBaseUrl(baseUrl);
        githubProperties.setUserInfoUri("");
        githubProperties.setTokenUri("/login/oauth/access_token");
        githubProperties.setClientId("testClientId");
        githubProperties.setClientSecret("testClientSecret");

        githubApiServiceImpl = new GithubApiServiceImpl(githubProperties, WebClient.builder());
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

    @Test
    @DisplayName("Debe autenticar correctamente y devolver el perfil de usuario")
    void authenticate_ShouldReturnUserData_WhenSuccessful() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"gho_test123\"}")
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"login\":\"testUser\"}")
                .addHeader("Content-Type", "application/json"));

        GithubUserRequestDto requestDto = new GithubUserRequestDto("code123");

        StepVerifier.create(githubApiServiceImpl.authenticate(requestDto))
                .expectNextMatches(userData -> userData.username().equals("testUser"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el código de GitHub es inválido")
    void authenticate_ShouldReturnError_WhenTokenIsMissing() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"error\":\"bad_verification_code\"}")
                .addHeader("Content-Type", "application/json"));

        GithubUserRequestDto requestDto = new GithubUserRequestDto("wrong_code");

        StepVerifier.create(githubApiServiceImpl.authenticate(requestDto))
                .expectError(GithubUnavailableException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el perfil de usuario falla (500)")
    void authenticate_ShouldReturnError_WhenProfileFails() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"access_token\":\"gho_test123\"}")
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500));

        GithubUserRequestDto requestDto = new GithubUserRequestDto("code123");

        StepVerifier.create(githubApiServiceImpl.authenticate(requestDto))
                .expectError(GithubUnavailableException.class)
                .verify();
    }
}
