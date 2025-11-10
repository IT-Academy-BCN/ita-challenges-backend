package com.itachallenge.userinteraction.service.favorite;

import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.userinteraction.document.favorite.FavoriteDocument;
import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }
    private Mono<UUID> parseAndValidateUUID(String id) {

        if (id == null || id.isEmpty()) {
            return Mono.error(new BadUUIDException("Invalid ID format"));
        }

        try {
            return Mono.just(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            return Mono.error(new BadUUIDException("Invalid ID format"));
        }
    }

@Override
    public Mono<Set<UUID>> getUserFavorites(String userId) {
        return parseAndValidateUUID(userId)
                .flatMap(userUuid ->
                        favoriteRepository.findByUserId(userUuid) // returns Flux<FavoriteDocument>
                                .map(FavoriteDocument::getChallengeId) // get only challenge IDs
                                .collect(Collectors.toSet())           // collect all into a Set<UUID>
                );
    }


}
