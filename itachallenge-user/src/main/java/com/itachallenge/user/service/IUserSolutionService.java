package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.dtos.UserSolutionDto;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;

public interface IUserSolutionService {

    Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String id, String idChallenge, String idLanguage);
    Mono<UserSolutionScoreDto> addSolution(UserSolutionDto userSolutionDto);
    Mono<UserSolutionDocument> markAsBookmarked(String uuidChallenge, String uuidLanguage, String uuidUser, boolean bookmarked);
    Flux<UserSolutionDto> showAllUserSolutions(UUID userUuid);

/* de rama 533b
   // Flux<SolutionScoreDto> getScore(String idUser, String idChallenge, String idSolution);
//    Mono<SolutionScoreDto> getScore(String idUser, String idChallenge, String idSolution);

//     Mono<UserSolScoreDto> getScore(String idUser, String idChallenge, String idSolution);
//Flux<UserSolScoreDto> getScore(String idUser, String idChallenge, String idSolution);
Mono<Integer> getScore(String idUser, String idChallenge, String idSolution);
} */
