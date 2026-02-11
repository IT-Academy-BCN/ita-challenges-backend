package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDocument> getUser(String githubUsername);

    @Deprecated
    Mono<Boolean> addChallengeToBookmarks(String userId, String challengeId);

    @Deprecated
    Mono<Boolean> deleteChallengeFromBookmarks(String userId, String challengeId);

    Mono<UserDocument> getUserById(String userId);
}
