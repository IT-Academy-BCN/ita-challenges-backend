package com.itachallenge.githubcore.service;

import reactor.core.publisher.Mono;
import java.util.Map;

public interface GithubOAuthService {
    Mono<Map<String, Object>> validateTokenWithGithub(String token);
    Mono<String> exchangeCodeForToken(String code);
}
