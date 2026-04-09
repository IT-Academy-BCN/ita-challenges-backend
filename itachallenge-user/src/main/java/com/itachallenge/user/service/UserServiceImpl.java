package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.common.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDocument> getUser(String githubUsername) {
        return userRepository.findByUsername(githubUsername)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    @Override
    public Mono<UserDocument> getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }
}
