package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.ChallengeDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveSortingRepository<ChallengeDocument, UUID> {

    Mono<Boolean> existsByUuid(UUID uuid);
    Mono<ChallengeDocument> findByUuid(UUID uuid);
    Flux<ChallengeDocument> findByLevel(String level);
    Mono<ChallengeDocument> findByTitle(String title);
    Flux<ChallengeDocument> findAllByUuidNotNull(Pageable pageable);
    Flux<ChallengeDocument> findAllByResourcesContaining(UUID idResource);
    Mono<Void> deleteByUuid(UUID uuid);
    Mono<ChallengeDocument> save(ChallengeDocument challenge);
    Flux<ChallengeDocument> saveAll(Flux<ChallengeDocument> challengeDocumentFlux);
    Flux<ChallengeDocument> findByLevelAndLanguages_IdLanguage(String Level, UUID idLanguage);
    Flux<ChallengeDocument> findByLanguages_IdLanguage(UUID idLanguage);
    Flux<ChallengeDocument> findByLanguages_LanguageName(String languageName);

}
