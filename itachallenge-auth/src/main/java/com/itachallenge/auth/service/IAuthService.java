package com.itachallenge.auth.service;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface IAuthService {

    Mono<Map<String, Object>> validateTokenWithGithub(String token);
    Mono<String> exchangeCodeForToken(String code);

}
