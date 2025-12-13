package com.itachallenge.userinteraction.service.favorite;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public interface FavoriteService {
    Mono<Boolean> addChallengeToFavorites(String userId, String challengeId);

    Mono<Set<UUID>> getUserFavorites(String userId);

    Mono<Boolean> deleteChallengeFromFavorites(String userId, String challengeId);
}
