package com.itachallenge.user.service;

import com.itachallenge.user.dtos.SolutionScoreDto;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IUserScoreService {
    Mono<SolutionScoreDto> addScore(UUID idUser, UUID idChallenge, UUID idSolution);
    SolutionScoreDto convertToSolutionScoreDto(UserSolutionDocument solution);
    Mono<SolutionUserDto<UserScoreDto>> getChallengeById(UUID userId, UUID challengeId, UUID languageId);
    Mono<SolutionUserDto<UserScoreDto>> getUserScoreByUserId(String userId, String challengeId, String languageId);
}


