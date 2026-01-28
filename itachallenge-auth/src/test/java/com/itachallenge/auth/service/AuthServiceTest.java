package com.itachallenge.auth.service;

import com.itachallenge.githubcore.dto.GithubAuthRequestDto;
import com.itachallenge.githubcore.dto.GithubAuthResponseDto;
import com.itachallenge.githubcore.service.GithubApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private GithubApiService githubApiService;

    @InjectMocks
    private AuthService authService;

    private final String testCode = "test-auth-code";
    private final String githubUsername = "octocat";

    @Test
    void authenticateWithGithub_Successful() {
        GithubAuthResponseDto mockResponse = new GithubAuthResponseDto(githubUsername);
        when(githubApiService.authenticate(any(GithubAuthRequestDto.class)))
                .thenReturn(Mono.just(mockResponse));

        Mono<Map<String, Object>> result = authService.authenticateWithGithub(testCode);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(true, response.get("isValid"));
                    assertEquals(githubUsername, response.get("username"));
                })
                .verifyComplete();
    }

    @Test
    void authenticateWithGithub_ErrorInLibrary_ReturnsInvalid() {
        when(githubApiService.authenticate(any(GithubAuthRequestDto.class)))
                .thenReturn(Mono.error(new RuntimeException("GitHub error")));

        Mono<Map<String, Object>> result = authService.authenticateWithGithub(testCode);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(false, response.get("isValid"));
                    assertEquals(null, response.get("username"));
                })
                .verifyComplete();
    }

    @Test
    void authenticateWithGithub_NullUsername_ReturnsInvalid() {
        GithubAuthResponseDto mockResponse = new GithubAuthResponseDto(null);
        when(githubApiService.authenticate(any(GithubAuthRequestDto.class)))
                .thenReturn(Mono.just(mockResponse));

        Mono<Map<String, Object>> result = authService.authenticateWithGithub(testCode);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(false, response.get("isValid"));
                    assertEquals(null, response.get("username"));
                })
                .verifyComplete();
    }

    @Test
    void login_ReturnsUsernameG() {
        String validCode = "valid-code";
        String githubUsername = "octocat";
        GithubAuthResponseDto mockResponse = new GithubAuthResponseDto(githubUsername);

        when(githubApiService.authenticate(any(GithubAuthRequestDto.class)))
                .thenReturn(Mono.just(mockResponse));

        Mono<Map<String, Object>> result = authService.authenticateWithGithub(validCode);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(true, response.get("isValid"));
                    assertEquals(githubUsername, response.get("username"));
                })
                .verifyComplete();
    }

    @Test
    void login_ReturnsInvalidG() {
        String expiredCode = "expired-code";
        when(githubApiService.authenticate(any(GithubAuthRequestDto.class)))
                .thenReturn(Mono.error(new RuntimeException("Bad credentials")));

        Mono<Map<String, Object>> result = authService.authenticateWithGithub(expiredCode);

        StepVerifier.create(result)
                .assertNext(response -> {
                    // El onErrorResume del Service debe capturar esto y devolver isValid: false
                    assertEquals(false, response.get("isValid"));
                    assertEquals(null, response.get("username"));
                })
                .verifyComplete();
    }

    @Test
    void authenticateWithGithub_UnexpectedResponse_ReturnsError() {
        GithubAuthResponseDto mockResponse = new GithubAuthResponseDto(null);

        when(githubApiService.authenticate(any(GithubAuthRequestDto.class)))
                .thenReturn(Mono.just(mockResponse));

        Mono<Map<String, Object>> result = authService.authenticateWithGithub("any-code");

        StepVerifier.create(result)
                .assertNext(response -> {
                    // Gracias a la nueva lógica, esto ahora será false
                    assertEquals(false, response.get("isValid"));
                    assertEquals(null, response.get("username"));
                })
                .verifyComplete();
    }
}
