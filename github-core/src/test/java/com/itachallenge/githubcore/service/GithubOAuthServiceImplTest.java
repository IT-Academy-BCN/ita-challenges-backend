package com.itachallenge.githubcore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    class GithubOAuthServiceImplTest {

        private MockWebServer mockWebServer;
        private GithubOAuthServiceImpl githubOAuthService;

        @BeforeEach
        void setUp() throws IOException {
            mockWebServer = new MockWebServer();
            mockWebServer.start();

            String baseUrl = mockWebServer.url("").toString();
            String githubTokenUri = baseUrl + "login/oauth/access_token";
            String githubUserInfoUri = baseUrl + "user";

            githubOAuthService = new GithubOAuthServiceImpl(
                    WebClient.builder(),
                    githubTokenUri,
                    githubUserInfoUri,
                    "test-client-id",
                    "test-client-secret",
                    new ObjectMapper()
            );
        }

        @AfterEach
        void tearDown() throws IOException {
            mockWebServer.shutdown();
        }

        @Test
        void exchangeCodeForToken_Successful() throws InterruptedException {
            String code = "auth-code";
            String mockResponse = "{\"access_token\": \"github-access-token\"}";

            mockWebServer.enqueue(new MockResponse()
                    .setBody(mockResponse)
                    .setResponseCode(200)
                    .addHeader("Content-Type", "application/json"));

            Mono<String> result = githubOAuthService.exchangeCodeForToken(code);

            StepVerifier.create(result)
                    .expectNext(mockResponse) // 👈 recorda: core retorna JSON cru
                    .verifyComplete();

            RecordedRequest request = mockWebServer.takeRequest();
            assertEquals("/login/oauth/access_token", request.getRequestUrl().encodedPath());
            assertEquals("application/json", request.getHeader("Accept"));
        }

        @Test
        void exchangeCodeForToken_InvalidCode_ReturnsError() {
            String code = "invalid-code";
            String mockResponse = "{\"error\": \"bad_verification_code\"}";

            mockWebServer.enqueue(new MockResponse()
                    .setBody(mockResponse)
                    .setResponseCode(400)
                    .addHeader("Content-Type", "application/json"));

            Mono<String> result = githubOAuthService.exchangeCodeForToken(code);

            StepVerifier.create(result)
                    .expectError(WebClientResponseException.BadRequest.class)
                    .verify();
        }

        @Test
        void exchangeCodeForToken_NetworkFailure_ReturnsError() {
            String code = "auth-code";

            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(500));

            Mono<String> result = githubOAuthService.exchangeCodeForToken(code);

            StepVerifier.create(result)
                    .expectError(WebClientResponseException.class)
                    .verify();
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

            Mono<Map<String, Object>> result = githubOAuthService.validateTokenWithGithub(validToken);

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

        @Test
        void validateTokenWithGithub_ExpiredToken_ReturnsInvalid() {
            String expiredToken = "expired-token";
            String mockResponse = "{\"message\": \"Bad credentials\"}";

            mockWebServer.enqueue(new MockResponse()
                    .setBody(mockResponse)
                    .setResponseCode(401)
                    .addHeader("Content-Type", "application/json"));

            Mono<Map<String, Object>> result = githubOAuthService.validateTokenWithGithub(expiredToken);

            StepVerifier.create(result)
                    .assertNext(response -> assertEquals(false, response.get("isValid")))
                    .verifyComplete();
        }

        @Test
        void validateTokenWithGithub_UnexpectedResponse_ReturnsError() {
            String token = "valid-token";
            String mockResponse = "{\"unexpected_key\": \"unexpected_value\"}";

            mockWebServer.enqueue(new MockResponse()
                    .setBody(mockResponse)
                    .setResponseCode(200)
                    .addHeader("Content-Type", "application/json"));

            Mono<Map<String, Object>> result = githubOAuthService.validateTokenWithGithub(token);

            StepVerifier.create(result)
                    .assertNext(response -> assertEquals(false, response.get("isValid")))
                    .verifyComplete();
        }
    }
