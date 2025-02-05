package com.itachallenge.user.service;

import com.itachallenge.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    UserRepository userRepository;

    public Mono<ResponseEntity<String>> isMentor(Mono<String> githubUsername){
        return githubUsername.flatMap(username -> userRepository.findUsername(username)
                .map(existingUsername -> ResponseEntity.status(HttpStatus.OK).body(existingUsername))
                .switchIfEmpty(Mono.fromRunnable(() -> logger.warn("Unauthorized access attempt for username '{}'", githubUsername))
                        .then(Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username is not related to a mentor.")))));

    }

}
