package com.itachallenge.user.service;

import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import reactor.core.publisher.Mono;

public interface IUserSolutionService {

    Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String id, String idChallenge, String idLanguage);

    Mono<UserSolutionScoreDto> addSolution(String id,
                                           String idChallenge,
                                           String idLanguage,
                                           String status,
                                           String solution);

//    public Mono<UserSolutionScoreDto> saveSolution(String idUser,
//                                                   String idChallenge,
//                                                   String idLanguage,
//                                                   String status,
//                                                   String solutionText);

}
