package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.SolutionDto;
import com.itachallenge.challenge.dto.LanguageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IChallengeService {

    Mono<GenericResultDto<ChallengeDto>> getChallengeById(String id);

    Mono<GenericResultDto<String>> removeResourcesByUuid(String id);

    //Mono<GenericResultDto<ChallengeDto>> getAllChallenges();

    Mono<GenericResultDto<LanguageDto>> getAllLanguages();
    Mono<GenericResultDto<SolutionDto>> getSolutions(String idChallenge, String idLanguage);

   // Flux<ChallengeDto> getChallengesByPage(int pageNumber, int pageSize);
    Flux<ChallengeDto> getAllChallenges(int pageNumber, int pageSize);
}
