package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface LanguageService {

    Flux<ChallengeDocument> filterByLanguage(Flux<ChallengeDocument> challenge, Optional<String> idLanguage);



}
