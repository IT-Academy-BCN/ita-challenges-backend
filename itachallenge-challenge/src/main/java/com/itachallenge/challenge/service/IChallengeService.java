package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface IChallengeService {

    Mono<GenericResultDto<ChallengeDto>> getChallengeById(String id);
    Mono<GenericResultDto<String>> removeResourcesByUuid(String id);
    Mono<GenericResultDto<LanguageDto>> getAllLanguages();
    Mono<GenericResultDto<SolutionDto>> getSolutions(String idChallenge, String idLanguage);
    Mono<SolutionDto> addSolution(SolutionDto solutionDto);
    Flux<ChallengeDto> getAllChallenges(int offset, int limit);
    Mono<GenericResultDto<ChallengeDto>> getChallengesByLanguageAndDifficulty(String idLanguage, String difficulty);
    Mono<GenericResultDto<ChallengeDto>> getRelatedChallenges(String id);
}
