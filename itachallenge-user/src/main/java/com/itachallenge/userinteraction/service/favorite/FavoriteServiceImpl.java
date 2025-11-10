package com.itachallenge.userinteraction.service.favorite;

import com.itachallenge.userinteraction.document.favorite.FavoriteDocument;
import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public class FavoriteServiceImpl {
    private final FavoriteRepository favoriteRepository;



    public FavoriteServiceImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public Flux<FavoriteDocument> getUserFavorites(String userId) {
        return favoriteRepository.findByUserId(UUID.fromString(userId));
    }


}
