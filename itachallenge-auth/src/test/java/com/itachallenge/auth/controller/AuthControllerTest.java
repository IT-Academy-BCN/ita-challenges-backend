package com.itachallenge.auth.controller;

import com.itachallenge.auth.service.IAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(AuthController.class)
@ActiveProfiles("test")
class AuthControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    Environment env;

    @MockBean
    private IAuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void authenticateWithGithub_ValidCode_ReturnsUsername() {
        String validCode = "valid-code";
        String accessToken = "valid-token";
        String githubUsername = "octocat";
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", true);
        validationResult.put("username", githubUsername);

        when(authService.exchangeCodeForToken(validCode)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken)).thenReturn(Mono.just(validationResult));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .bodyValue(Map.of("code", validCode))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(true)
                .jsonPath("$.username").isEqualTo(githubUsername);
    }

    @Test
    void authenticateWithGithub_InvalidCode_ReturnsUnauthorized() {
        String invalidCode = "invalid-code";
        String accessToken = "invalid-token";
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", false);
        validationResult.put("username", null);

        when(authService.exchangeCodeForToken(invalidCode)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken)).thenReturn(Mono.just(validationResult));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .bodyValue(Map.of("code", invalidCode))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").isEmpty();
    }

    @Test
    void authenticateWithGithub_TokenExchangeError_ReturnsInternalServerError() {
        String invalidCode = "invalid-code";

        when(authService.exchangeCodeForToken(invalidCode))
                .thenReturn(Mono.error(new RuntimeException("Token exchange failed")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .bodyValue(Map.of("code", invalidCode))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").isEmpty();
    }

    @Test
    void authenticateWithGithub_TokenValidationError_ReturnsInternalServerError() {
        String validCode = "valid-code";
        String accessToken = "valid-token";

        when(authService.exchangeCodeForToken(validCode)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken))
                .thenReturn(Mono.error(new RuntimeException("Token validation failed")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .bodyValue(Map.of("code", validCode))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").isEmpty();
    }

    @Test
    void authenticateWithGithub_MissingRequestBody_ReturnsBadRequest() {
        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getVersionTest() {
        String expectedVersion = env.getProperty("spring.application.version");

        webTestClient.get()
                .uri("/itachallenge/api/v1/auth/version")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.application_name").isEqualTo("itachallenge-auth")
                .jsonPath("$.version").isEqualTo("1.0.0-RELEASE");
    }
}