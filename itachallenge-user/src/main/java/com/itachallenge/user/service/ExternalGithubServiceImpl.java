package com.itachallenge.user.service;

import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.service.GithubApiService;
import reactor.core.publisher.Mono;

public class ExternalGithubServiceImpl implements ExternalGithubService {

    private final GithubApiService githubApiService;

    public ExternalGithubServiceImpl(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Mono<Boolean> userExists(String username) {
        return githubApiService.userExists(username)
                .map(status -> status != GithubUserStatus.NOT_FOUND);
    }
}
