package com.itachallenge.gamification.service;


import com.itachallenge.challenge.dto.gamification.RankingResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PointsService {

    Flux<RankingResponseDto> getRankingDescOrder();
}
