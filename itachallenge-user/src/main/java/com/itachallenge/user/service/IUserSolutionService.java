package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.*;
import com.itachallenge.user.enums.ChallengeStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IUserSolutionService {

    Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String id, String idChallenge, String idLanguage);
    Mono<UserSolutionScoreDto> addSolution(UserSolutionDto userSolutionDto);
    Mono<UserSolutionDocument> markAsBookmarked(String uuidChallenge, String uuidLanguage, String uuidUser, boolean bookmarked);
    Flux<UserSolutionDto> showAllUserSolutions(UUID userUuid);
    Mono<UserSolScoreDto> getScoreFromScoreMicro(String idUser, String idChallenge, String idSolution);
}
