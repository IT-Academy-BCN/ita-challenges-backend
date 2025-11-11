package com.itachallenge.userinteraction.service.favorite;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public interface FavoriteService {

    Mono<Set<UUID>> getUserFavorites(String userId);
    Mono<Boolean> addChallengeToFavorites(String userId, String challengeId);
    Mono<Boolean> deleteChallengeFromFavorites(String userId, String challengeId);
    Mono<Boolean> addToFavorites(UUID userUuid, UUID challengeUuid);
    Mono<Boolean> deleteFromFavorites(UUID userId, UUID challengeUuid);
}
