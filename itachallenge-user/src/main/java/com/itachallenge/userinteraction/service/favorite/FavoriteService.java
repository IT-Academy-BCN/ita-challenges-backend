package com.itachallenge.userinteraction.service.favorite;

import com.itachallenge.user.dto.userinteraction.favorite.FavoriteResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

public interface FavoriteService {

    public Flux<FavoriteResponseDto> getUserFavorites(String userId);
}
