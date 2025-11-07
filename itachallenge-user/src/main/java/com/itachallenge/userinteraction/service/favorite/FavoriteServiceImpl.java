package com.itachallenge.userinteraction.service.favorite;

import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;

public class FavoriteServiceImpl {
    private final FavoriteRepository favoriteRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }


}
