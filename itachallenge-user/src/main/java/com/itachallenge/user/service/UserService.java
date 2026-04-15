package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDocument> getUser(String githubUsername);

    Mono<UserDocument> getUserById(String userId);
}
