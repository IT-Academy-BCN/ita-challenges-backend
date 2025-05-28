package com.itachallenge.auth.service;


import com.itachallenge.auth.config.ClientConfig;
import com.itachallenge.auth.config.GithubClientProperties;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthServiceTest {

    private MockWebServer mockWebServer;
    private AuthService authService;
    private GithubClientProperties githubClientProperties;
    private WebClient webClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("").toString();
        String githubTokenUri = baseUrl + "login/oauth/access_token";
        String githubUserInfoUri = baseUrl + "user";

        ClientConfig testClientConfig = new ClientConfig();
        testClientConfig.setClientId("test-client-id");
        testClientConfig.setClientSecret("test-client-secret");

        GithubClientProperties githubClientProperties = new GithubClientProperties();
        Map<String, ClientConfig> envs = new HashMap<>();
        envs.put("local", testClientConfig);
        githubClientProperties.setEnvironments(envs);

        authService = new AuthService(
                WebClient.builder(),
                githubClientProperties,
                githubTokenUri,
                githubUserInfoUri);
        //"test-client-id",
        //"test-client-secret");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    public Mono<String> exchangeCodeForToken(String code, String environment) {
        // Usa "local" por defecto si no se proporciona el environment
        String env = (environment == null || environment.isBlank()) ? "local" : environment;

        ClientConfig config = githubClientProperties.getEnvironments().get(env);
        if (config == null) {
            return Mono.error(new IllegalArgumentException("No client configuration found for environment: " + env));
        }

        // Construye la URI destino (GitHub o MockWebServer) aquí directamente
        String tokenUri = "https://github.com/login/oauth/access_token";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", config.getClientId());
        requestBody.put("client_secret", config.getClientSecret());
        requestBody.put("code", code);

        return webClient.post()
                .uri(tokenUri)
                .headers(headers -> headers.set("Accept", "application/json"))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    Object token = response.get("access_token");
                    if (token instanceof String tokenStr && !tokenStr.isEmpty()) {
                        return Mono.just(tokenStr);
                    } else {
                        return Mono.error(new IllegalStateException("Access token not found in response"));
                    }
                });
    }

    @Test
    void exchangeCodeForToken_InvalidCode_ReturnsError() {
        String code = "invalid-code";
        String redirectUri = "http://localhost:4200/ita-challenge/challenges"; // esto debe mapearse a "local"

        String mockResponse = "{\"error\": \"bad_verification_code\"}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json"));

        Mono<String> result = authService.exchangeCodeForToken(code, redirectUri);

        StepVerifier.create(result)
                .expectError(WebClientResponseException.BadRequest.class)
                .verify();
    }

    @Test
    void exchangeCodeForToken_NetworkFailure_ReturnsError() {
        String code = "auth-code";
        String redirectUri = "http://localhost:4200/ita-challenge/challenges"; // <-- debe mapear a "local"

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)); // Simula error de red

        Mono<String> result = authService.exchangeCodeForToken(code, redirectUri);

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

}
