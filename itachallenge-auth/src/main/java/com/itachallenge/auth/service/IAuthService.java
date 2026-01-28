package com.itachallenge.auth.service;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface IAuthService {

    Mono<Map<String, Object>> authenticateWithGithub(String token);

}
