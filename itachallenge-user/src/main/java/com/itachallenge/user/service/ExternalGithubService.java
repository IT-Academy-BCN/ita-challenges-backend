package com.itachallenge.user.service;

import reactor.core.publisher.Mono;

public interface ExternalGithubService {
    Mono<Boolean> userExists(String username);
}
