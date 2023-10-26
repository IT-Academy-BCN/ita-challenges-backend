package com.itachallenge.user.service;

import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface IServiceChallengeStatistics {
    Mono<List<ChallengeStatisticsDto>> getChallengeStatistics(List<UUID> challengeIds);
}
