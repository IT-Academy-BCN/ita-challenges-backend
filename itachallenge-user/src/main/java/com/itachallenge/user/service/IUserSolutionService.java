package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserSolutionService {

    Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String id, String idChallenge, String idLanguage);
    Mono<UserSolutionScoreDto> addSolution(UserSolutionDto userSolutionDto);
    Mono<UserSolutionDocument> markAsBookmarked(String uuidChallenge, String uuidLanguage, String uuidUser, boolean bookmarked);
    Flux<ResponseEntity<UserSolutionDocument>> addScore(String idUser, String idChallenge, String idSolution); // Phase 1

}
