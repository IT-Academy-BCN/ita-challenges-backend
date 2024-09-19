package com.itachallenge.challenge.repository;


import com.itachallenge.challenge.document.ChallengeDocument;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.mongodb.repository.Query;

import java.util.UUID;



@Repository
public interface ChallengeRepository extends ReactiveSortingRepository<ChallengeDocument, UUID> {

    Mono<Boolean> existsByUuid(UUID uuid);
    Mono<ChallengeDocument> findByUuid(UUID uuid);
    Flux<ChallengeDocument> findByLevel(String level);
    @Query(value = "{}", fields = "{'testingValues':0}")
    Flux<ChallengeDocument> findAllByUuidNotNullExcludingTestingValues();
    Flux<ChallengeDocument> findAllByResourcesContaining(UUID idResource);
    Mono<Long> count();
    Mono<Void> deleteByUuid(UUID uuid);
    Mono<ChallengeDocument> save(ChallengeDocument challenge);
    Flux<ChallengeDocument> saveAll(Flux<ChallengeDocument> challengeDocumentFlux);
    @Query(value = "{ 'level' : ?0, 'languages.idLanguage' : ?1 }", fields = "{'testingValues':0}")
    Flux<ChallengeDocument> findByLevelAndLanguages_IdLanguage(String level, UUID idLanguage);
    @Query(value = "{ 'languages.idLanguage' : ?0 }", fields = "{'testingValues':0}")
    Flux<ChallengeDocument> findByLanguages_IdLanguage(UUID idLanguage);
    Flux<ChallengeDocument> findByLanguages_LanguageName(String languageName);
}
