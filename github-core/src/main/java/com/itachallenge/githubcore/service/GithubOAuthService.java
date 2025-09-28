package com.itachallenge.githubcore.service;

import reactor.core.publisher.Mono;

public interface GithubOAuthService {
    Mono<String> exchangeCodeForToken(String code);
    Mono<String> getUsernameFromToken(String token);
}
