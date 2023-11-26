package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.Locale;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveSortingRepository<ChallengeDocument, UUID> {

    Mono<Boolean> existsByUuidAndLocale(UUID uuid, Locale locale);
    Mono<ChallengeDocument> findByUuidAndLocale(UUID uuid, Locale locale);
    Flux<ChallengeDocument> findByLevelAndLocale(String level, Locale locale);
    Mono<ChallengeDocument> findByTitle(String title);
    Flux<ChallengeDocument> findAllByUuidNotNullAndLocale(Locale locale);
    Flux<ChallengeDocument> findAllByResourcesContainingAndLocale(UUID idResource, Locale locale);
    Mono<Void> deleteByUuidAndLocale(UUID uuid, Locale locale);
    Mono<ChallengeDocument> save(ChallengeDocument challenge);
    Flux<ChallengeDocument> saveAll(Flux<ChallengeDocument> challengeDocumentFlux);
    Flux<ChallengeDocument> findByLevelAndLanguages_IdLanguageAndLocale(String level, UUID idLanguage, Locale locale);
    Flux<ChallengeDocument> findByLanguages_IdLanguageAndLocale(UUID idLanguage, Locale locale);
    Flux<ChallengeDocument> findByLanguages_LanguageNameAndLocale(String languageName, Locale locale);

}
