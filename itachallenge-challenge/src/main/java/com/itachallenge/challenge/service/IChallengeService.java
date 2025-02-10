package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

public interface IChallengeService {

    Mono<ChallengeDto> getChallengeById(String id);

    Mono<GenericResultDto<LanguageDto>> getAllLanguages();

    Mono<GenericResultDto<SolutionDto>> getSolutions(String idChallenge, String idLanguage);

    Mono<SolutionDto> addSolution(SolutionDto solutionDto);

    Mono<GenericResultDto<ChallengeDto>> getAllChallenges(int offset, int limit);

    Mono<GenericResultDto<ChallengeDto>> getChallengesByLanguageOrDifficulty(Optional<String> idLanguage, Optional<String> level, int offset, int limit);

    Mono<String> updateResourceByUuid(String id, Map<String, Object> updates);

    Mono<ChallengeDto> addChallenge(ChallengeCreateDto challengeCreateDto);

    Mono<DeleteResponseDto> deleteChallengeById(String id);
}
