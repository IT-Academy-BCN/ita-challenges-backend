package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<String> isMentorUsername(Mono<String> githubUsernameMono) {
        return githubUsernameMono.flatMap(username ->
                userRepository.findByUsername(username)
                        .map(UserDocument::getUsername)
        );
    }

    public Mono<Boolean> isMentor(Mono<String> githubUsernameMono) {
        return githubUsernameMono.flatMap(userRepository::existsByUsername);
    }

}
