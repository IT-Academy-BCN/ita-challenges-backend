package com.itachallenge.auth.controller;

import com.itachallenge.auth.service.AuthService;
import com.itachallenge.auth.service.IAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

    // Old tests
    @Test
    void validateTokenOK() {
        String validToken = "validToken";
        when(authService.validateWithSSO(validToken)).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(authController.validateToken(validToken))
                .expectNextMatches(responseEntity ->
                        responseEntity.getStatusCode() == HttpStatus.OK &&
                                responseEntity.getBody().equals("Token is valid"))
                .verifyComplete();
    }


    @Test
    void validateTokenNotOK() {
        String invalidToken = "invalidToken";
        when(authService.validateWithSSO(invalidToken)).thenReturn(Mono.just(false));

        StepVerifier.create((authController.validateToken(invalidToken)))
                .expectNextMatches(responseEntity -> responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED &&
                        responseEntity.getBody().equals("Token is not valid"))
                .verifyComplete();
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