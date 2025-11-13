package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public interface UserService {
    Mono<UserDocument> getUser(String githubUsername);

    Mono<Boolean> addChallengeToFavorites(String userId, String challengeId);

    Mono<Boolean> addChallengeToBookmarks(String userId, String challengeId);

    Mono<Boolean> deleteChallengeFromFavorites(String userId, String challengeId);

    Mono<Boolean> deleteChallengeFromBookmarks(String userId, String challengeId);

    Mono<Set<UUID>> getUserBookmarks(String userId);
    
    Mono<UserDocument> getUserById(String userId);
}
