package com.itachallenge.userinteraction.service.bookmark;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public interface BookmarkService {
    Mono<Set<UUID>> getUserBookmarks(String userId);

    Mono<Boolean> addChallengeToBookmarks(String userId, String challengeId);

    Mono<Boolean> deleteChallengeFromBookmarks(String userId, String challengeId);
}
