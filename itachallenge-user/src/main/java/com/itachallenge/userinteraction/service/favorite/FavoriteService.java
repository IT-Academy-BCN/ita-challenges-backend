package com.itachallenge.userinteraction.service.favorite;

import com.itachallenge.user.dto.userinteraction.favorite.FavoriteResponseDto;
import reactor.core.publisher.Flux;

public interface FavoriteService {

    public Flux<FavoriteResponseDto> getUserFavorites(String userId);
}
