package com.itachallenge.auth.service;

import com.itachallenge.auth.dto.User;
import reactor.core.publisher.Mono;

public interface IUserService {

    Mono<User> fetchUserData(String githubUsername);

    Mono<String> callUserTest();

}
