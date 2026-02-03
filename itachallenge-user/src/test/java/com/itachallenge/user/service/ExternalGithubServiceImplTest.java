package com.itachallenge.user.service;

import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.exception.GithubUnavailableException;
import com.itachallenge.githubcore.service.GithubApiService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalGithubServiceImplTest {

    @Mock
    private GithubApiService githubApiService;

    @InjectMocks
    private ExternalGithubServiceImpl externalGithubService;

    @Test
    @DisplayName("Should return true when GitHub user exists")
    void testUserExistsReturnsTrue() {
        when(githubApiService.userExists("someUser"))
                .thenReturn(Mono.just(GithubUserStatus.FOUND));

        StepVerifier.create(externalGithubService.userExists("someUser"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return false when GitHub user is NOT_FOUND")
    void testUserExistsReturnsFalse() {
        when(githubApiService.userExists("ghostUser"))
                .thenReturn(Mono.just(GithubUserStatus.NOT_FOUND));

        StepVerifier.create(externalGithubService.userExists("ghostUser"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should wrap API errors in GithubUnavailableException, preserving the cause")
    void testUserExistsPropagatesError() {
        when(githubApiService.userExists(anyString()))
                .thenReturn(Mono.error(new GithubUnavailableException("GitHub down")));

        StepVerifier.create(externalGithubService.userExists("anyUser"))
                .expectError()
                .verify();
    }
}
