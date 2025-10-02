package com.itachallenge.user.service;

import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.exception.GithubUnavailableException;
import com.itachallenge.githubcore.service.GithubApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ExternalGithubServiceImplTest {

    private GithubApiService githubApiService;
    private ExternalGithubServiceImpl externalGithubService;

    @BeforeEach
    void setUp() {
        githubApiService = Mockito.mock(GithubApiService.class);
        externalGithubService = new ExternalGithubServiceImpl(githubApiService);
    }

    @Test
    @DisplayName("Should return true when GitHub user exists")
    void testUserExistsReturnsTrue() {
        when(githubApiService.userExists(anyString()))
                .thenReturn(Mono.just(GithubUserStatus.FOUND));

        StepVerifier.create(externalGithubService.userExists("someUser"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return false when GitHub user is NOT_FOUND")
    void testUserExistsReturnsFalse() {
        when(githubApiService.userExists(anyString()))
                .thenReturn(Mono.just(GithubUserStatus.NOT_FOUND));

        StepVerifier.create(externalGithubService.userExists("ghostUser"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should propagate error when GitHub API fails")
    void testUserExistsPropagatesError() {
        when(githubApiService.userExists(anyString()))
                .thenReturn(Mono.error(new GithubUnavailableException("GitHub API down")));

        StepVerifier.create(externalGithubService.userExists("anyUser"))
                .expectErrorMatches(throwable ->
                        throwable instanceof GithubUnavailableException &&
                                throwable.getMessage().equals("GitHub API down"))
                .verify();
    }
}
