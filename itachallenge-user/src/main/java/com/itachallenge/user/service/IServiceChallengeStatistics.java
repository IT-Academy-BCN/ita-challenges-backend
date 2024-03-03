package com.itachallenge.user.service;

import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IServiceChallengeStatistics {
    Mono<ChallengeStatisticsDto> getChallengeStatistics(UUID challengeId);

    Mono<Float> getChallengeUsersPercentage(UUID challengeId);

    Mono<Integer> getChallengePopularity(UUID challengeId);
}
