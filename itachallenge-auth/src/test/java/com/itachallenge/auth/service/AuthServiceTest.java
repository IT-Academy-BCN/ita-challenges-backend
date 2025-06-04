package com.itachallenge.auth.service;


import com.itachallenge.auth.config.ClientConfig;
import com.itachallenge.auth.config.GithubClientProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        ClientConfig localConfig = new ClientConfig();
        localConfig.setClientId("local-client-id");
        localConfig.setClientSecret("local-client-secret");
        localConfig.setRedirectUri("http://localhost/callback");

        ClientConfig devConfig = new ClientConfig();
        devConfig.setClientId("dev-client-id");
        devConfig.setClientSecret("dev-client-secret");
        devConfig.setRedirectUri("https://dev.ita-challenges.eurecatacademy.org/callback");

        GithubClientProperties githubClientProperties = new GithubClientProperties();
        Map<String, ClientConfig> envMap = new HashMap<>();
        envMap.put("local", localConfig);
        envMap.put("dev",   devConfig);
        githubClientProperties.setEnvironments(envMap);

        authService = new AuthService(
                WebClient.builder(),
                githubClientProperties,
                githubTokenUri,
                githubUserInfoUri
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void exchangeCodeForToken_Successful_Local() throws InterruptedException {
        String code = "auth-code";
        String accessToken = "github-access-token";
        String mockResponse = "{\"access_token\": \"" + accessToken + "\"}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));
        Mono<String> result = authService.exchangeCodeForToken(code, "http://localhost:8080/callback");
        StepVerifier.create(result)
                .expectNext(accessToken)
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
                .setResponseCode(400) // Simulate GitHub rejecting the code
                .addHeader("Content-Type", "application/json"));

        Mono<String> result = authService.exchangeCodeForToken(code, "http://localhost:8080/callback");
        StepVerifier.create(result)
                .expectError(WebClientResponseException.BadRequest.class)
                .verify();
    }

    @Test
    void exchangeCodeForToken_NetworkFailure_ReturnsError() {
        String code = "auth-code";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500));
        String redirectUri = "http://localhost:4200/ita-challenge/challenges";
        Mono<String> result = authService.exchangeCodeForToken(code, redirectUri);

        StepVerifier.create(result)
                .expectError()
                .verify();
    }

    @Test
    void validateTokenWithGithub_ValidToken_ReturnsUsername() {
        AuthService authService = mock(AuthService.class);
        Map<String, Object> expectedResult = Map.of("isValid", true, "username", "octocat");
        when(authService.validateTokenWithGithub("valid-token"))
                .thenReturn(Mono.just(expectedResult));
        Mono<Map<String, Object>> resultMono = authService.validateTokenWithGithub("valid-token");
        StepVerifier.create(resultMono)
                .expectNextMatches(map -> map.get("isValid").equals(true))
                .verifyComplete();
        resultMono.subscribe(map -> assertTrue((Boolean) map.get("isValid")));
    }

    @Test
    void validateTokenWithGithub_ExpiredToken_ReturnsInvalid() {
        String expiredToken = "expired-token";
        String mockResponse = "{\"message\": \"Bad credentials\"}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json"));

        Mono<Map<String, Object>> result = authService.validateTokenWithGithub(expiredToken);

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

        Mono<Map<String, Object>> result = authService.validateTokenWithGithub(token);

        StepVerifier.create(result)
                .assertNext(response -> assertEquals(false, response.get("isValid")))
                .verifyComplete();
    }


    @Test
    void exchangeCodeForToken_UnknownEnvironment_ThrowsException() {
        String code = "auth-code";
        String unknownRedirectUri = "https://unknown-environment.com/callback";
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            authService.exchangeCodeForToken(code, unknownRedirectUri);
        });
        assertTrue(thrown.getMessage().contains("Unknown environment for redirect URI"));
    }
}
