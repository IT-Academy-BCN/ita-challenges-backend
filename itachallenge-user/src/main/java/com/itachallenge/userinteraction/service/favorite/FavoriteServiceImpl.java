package com.itachallenge.userinteraction.service.favorite;

import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class FavoriteServiceImpl {
    private final FavoriteRepository favoriteRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }


}
