package com.itachallenge.auth.service;

import com.itachallenge.auth.dto.User;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IUserService {

    Mono<ResponseEntity<User>> forwardUserDetails(String githubUsername);
    Mono<String> callUserTest();

}
