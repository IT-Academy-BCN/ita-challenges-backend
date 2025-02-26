package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<Boolean> isMentor(Mono<String> githubUsernameMono) {
        return githubUsernameMono.flatMap(userRepository::existsByUsername);
    }

    public Mono<UserDocument> getUser(Mono<String> githubUsernameMono) {
        return githubUsernameMono.flatMap(userRepository::findByUsername);
    }

}
