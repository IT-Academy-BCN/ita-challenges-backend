package com.itachallenge.user.service;

import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import reactor.core.publisher.Mono;

public interface IUserScoreService {

    Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String id, String idChallenge, String idLanguage);

}
