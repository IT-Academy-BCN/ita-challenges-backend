package com.itachallenge.githubcore.service;

import reactor.core.publisher.Mono;

public interface IGithubApiService {
    Mono<Boolean> userExists(String username);
}
