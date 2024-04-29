package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IChallengeService {

    Mono<ChallengeDto> getChallengeById(String id);
    Mono<String> removeResourcesByUuid(String id);
    Mono<GenericResultDto<LanguageDto>> getAllLanguages();
    Mono<GenericResultDto<SolutionDto>> getSolutions(String idChallenge, String idLanguage);
    Mono<SolutionDto> addSolution(SolutionDto solutionDto);
    Flux<GenericResultDto<ChallengeDto>> getAllChallenges(int offset, int limit);
    Mono<GenericResultDto<ChallengeDto>> getChallengesByLanguageAndDifficulty(String idLanguage, String difficulty);
    Mono<GenericResultDto<ChallengeDto>> getRelatedChallenges(String id, int offset, int limit);
    Mono<Map<String, Object>> getTestingParamsByChallengeIdAndLanguageId(String idChallenge, String idLanguage);

}
