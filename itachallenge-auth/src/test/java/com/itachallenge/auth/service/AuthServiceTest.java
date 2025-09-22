package com.itachallenge.auth.service;


import com.itachallenge.githubcore.service.GithubApiService;
import com.itachallenge.githubcore.service.GithubOAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthServiceTest {

    private GithubOAuthService githubOAuthService;
    private GithubApiService githubApiService;
    private ObjectMapper objectMapper;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        githubOAuthService = Mockito.mock(GithubOAuthService.class);
        githubApiService = Mockito.mock(GithubApiService.class);
        objectMapper = new ObjectMapper();

        authService = new AuthService(githubOAuthService, githubApiService, objectMapper);
    }

    @Test
    void exchangeCodeForToken_successfulFlow() {
        String code = "auth-code";
        String token = "valid-token";
        String response = "{\"access_token\":\"" + token + "\"}";

        Mockito.when(githubOAuthService.exchangeCodeForToken(code))
                .thenReturn(Mono.just(response));

        Mono<String> result = authService.exchangeCodeForToken(code);

        StepVerifier.create(result)
                .expectNext(token)
                .verifyComplete();
    }

    @Test
    void validateTokenWithGithub_successfulFlow() {
        String token = "valid-token";

        // mockem la resposta processada del core
        Mockito.when(githubOAuthService.validateTokenWithGithub(token))
                .thenReturn(Mono.just(Map.of("isValid", true, "username", "octocat")));

        Mono<Map<String, Object>> result = authService.validateTokenWithGithub(token);

        StepVerifier.create(result)
                .assertNext(map -> {
                    assertEquals(true, map.get("isValid"));
                    assertEquals("octocat", map.get("username"));
                })
                .verifyComplete();
    }

}
