package com.itachallenge.user.service;

import com.itachallenge.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Mono<String> isMentor(Mono<String> githubUsername) {
        return githubUsername.flatMap(username -> userRepository.findByUsernameReturnUsername(username)
                .switchIfEmpty(Mono.empty()));
    }
}
