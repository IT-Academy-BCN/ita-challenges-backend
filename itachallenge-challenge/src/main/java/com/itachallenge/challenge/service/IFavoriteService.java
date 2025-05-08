package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.FavoriteDto;
import reactor.core.publisher.Mono;

public interface IFavoriteService {

    Mono<FavoriteDto> addChallengeToFavorites(String challengeId, String userId);

    Mono<FavoriteDto> removeChallengeFromFavorites(String challengeId, String userId);
}