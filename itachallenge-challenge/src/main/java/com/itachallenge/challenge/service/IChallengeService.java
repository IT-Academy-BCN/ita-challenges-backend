package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IChallengeService {

    Mono<GenericResultDto<ChallengeDto>> getChallengeById(String id);
    Mono<GenericResultDto<String>> removeResourcesByUuid(String id);
    Mono<GenericResultDto<ChallengeDto>> getAllChallenges();
    Mono<GenericResultDto<LanguageDto>> getAllLanguages();

    Flux<ChallengeDto> getChallengesPaginated(int pageNumber, int pageSize);

    //Mono<PagingParametersDto<ChallengeDto>> getChallengesPaginated(int pageNumber, int pageSize);

}
