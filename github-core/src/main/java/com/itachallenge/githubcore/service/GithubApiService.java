package com.itachallenge.githubcore.service;

import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import reactor.core.publisher.Mono;

public interface GithubApiService {
    Mono<GithubUserStatus> userExists(String username);
}
