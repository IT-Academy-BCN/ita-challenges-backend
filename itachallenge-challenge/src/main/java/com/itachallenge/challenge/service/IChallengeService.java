package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.SolutionDto;
import com.itachallenge.challenge.dto.LanguageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface IChallengeService {

    Mono<ChallengeDto> getChallengeById(String id);
    Mono<String> removeResourcesByUuid(String id);
    Mono<GenericResultDto<LanguageDto>> getAllLanguages();
    Mono<GenericResultDto<SolutionDto>> getSolutions(String idChallenge, String idLanguage);
    Mono<SolutionDto> addSolution(SolutionDto solutionDto);
    Flux<ChallengeDto> getAllChallenges(int offset, int limit);
    Flux<ChallengeDto> getChallengesByLanguageOrDifficulty(Optional<String> idLanguage, Optional<String> difficulty, int offset, int limit);
    Mono<GenericResultDto<ChallengeDto>> getRelatedChallenges(String id, int offset, int limit);
}
