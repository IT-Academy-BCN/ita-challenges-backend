package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.RankingResponseDto;
import com.itachallenge.gamification.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {


    private final UserScoreRepository  userScoreRepository;
    Mono<RankingResponseDto> responseDto;


    public Flux<RankingResponseDto> getRankingAscOrder(){

        return userScoreRepository.findUsersRanking();
    }

}
