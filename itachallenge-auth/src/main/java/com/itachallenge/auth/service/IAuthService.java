package com.itachallenge.auth.service;

import reactor.core.publisher.Mono;

public interface IAuthService {

    Mono<Boolean> validateWithSSO(String token);

}
