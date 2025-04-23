package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface ILanguageService {

    Flux<ChallengeDocument> filterByLanguage(Flux<ChallengeDocument> challenge, Optional<String> idLanguage);
    Mono<GenericResultDto<LanguageDto>> getAllLanguages();
    Mono<LanguageDocument> findByIdLanguage(UUID id);
    Mono<LanguageDocument> findFirstByLanguageName(String languageName);



}
