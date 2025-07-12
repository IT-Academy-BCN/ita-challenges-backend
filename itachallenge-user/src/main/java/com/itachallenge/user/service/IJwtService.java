package com.itachallenge.user.service;

import reactor.core.publisher.Mono;

public interface IJwtService {
    Mono<String> extractRoleFromToken(String authHeader);
}