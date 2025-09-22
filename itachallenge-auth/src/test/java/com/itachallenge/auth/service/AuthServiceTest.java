package com.itachallenge.auth.service;

import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.service.GithubApiService;
import com.itachallenge.githubcore.service.GithubOAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private GithubOAuthService githubOAuthService;
    private GithubApiService githubApiService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        githubOAuthService = Mockito.mock(GithubOAuthService.class);
        githubApiService = Mockito.mock(GithubApiService.class);
        authService = new AuthService(githubOAuthService, githubApiService);
    }

    @Test
    void exchangeCodeForToken_Success() {
        String code = "auth-code";
        String token = "access-token";

        when(githubOAuthService.exchangeCodeForToken(code)).thenReturn(Mono.just(token));

        StepVerifier.create(authService.exchangeCodeForToken(code))
                .expectNext(token)
                .verifyComplete();

        verify(githubOAuthService, times(1)).exchangeCodeForToken(code);
    }

    @Test
    void exchangeCodeForToken_Error() {
        String code = "auth-code";

        when(githubOAuthService.exchangeCodeForToken(code))
                .thenReturn(Mono.error(new RuntimeException("GitHub error")));

        StepVerifier.create(authService.exchangeCodeForToken(code))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void validateTokenWithGithub_ValidToken() {
        String token = "valid-token";
        String username = "octocat";

        when(githubOAuthService.getUsernameFromToken(token)).thenReturn(Mono.just(username));

        StepVerifier.create(authService.validateTokenWithGithub(token))
                .assertNext(result -> {
                    assertEquals(true, result.get("isValid"));
                    assertEquals(username, result.get("username"));
                    assertEquals(token, result.get("token"));
                })
                .verifyComplete();

        verify(githubOAuthService, times(1)).getUsernameFromToken(token);
    }

    @Test
    void validateTokenWithGithub_InvalidToken() {
        String token = "bad-token";

        when(githubOAuthService.getUsernameFromToken(token))
                .thenReturn(Mono.error(new RuntimeException("Invalid token")));

        StepVerifier.create(authService.validateTokenWithGithub(token))
                .assertNext(result -> {
                    assertEquals(false, result.get("isValid"));
                    assertEquals(null, result.get("username"));
                })
                .verifyComplete();

        verify(githubOAuthService, times(1)).getUsernameFromToken(token);
    }

    @Test
    void checkUserExists_Found() {
        String username = "octocat";

        when(githubApiService.userExists(username)).thenReturn(Mono.just(GithubUserStatus.FOUND));

        StepVerifier.create(authService.checkUserExists(username))
                .expectNext(GithubUserStatus.FOUND)
                .verifyComplete();

        verify(githubApiService, times(1)).userExists(username);
    }

    @Test
    void checkUserExists_NotFound() {
        String username = "ghost";

        when(githubApiService.userExists(username)).thenReturn(Mono.just(GithubUserStatus.NOT_FOUND));

        StepVerifier.create(authService.checkUserExists(username))
                .expectNext(GithubUserStatus.NOT_FOUND)
                .verifyComplete();

        verify(githubApiService, times(1)).userExists(username);
    }
}
