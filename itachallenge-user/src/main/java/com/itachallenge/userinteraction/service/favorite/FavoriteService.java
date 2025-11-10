package com.itachallenge.userinteraction.service.favorite;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public interface FavoriteService {

    public Mono<Set<UUID>> getUserFavorites(String userId);
}
