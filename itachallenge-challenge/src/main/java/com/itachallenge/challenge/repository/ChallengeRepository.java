package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.ChallengeDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveMongoRepository<ChallengeDocument,UUID> {

    Mono<Boolean> existsByUuid(UUID uuid);
    Mono<ChallengeDocument> findByUuid(UUID uuid);
    Mono<Void> deleteByUuid(UUID uuid);
    Mono<ChallengeDocument> findByLevel(String level);
    Mono<ChallengeDocument> findByTitle(String title);
    Flux<ChallengeDocument> findAllByResourcesContaining(UUID idResource);




    /**
     * Find challenges with languages filters
     * @param language Languages to find
     * @return
     */
    Flux<ChallengeDocument> findByLanguages_LanguageNameIn(Set<String> language);

    /**
     * Find challenges with level filters
     * @param level Levels to find
     * @return
     */
    Flux<ChallengeDocument> findByLevels_LevelIn(Set<String> level);



    @Override
    Mono<ChallengeDocument> save (ChallengeDocument challenge);

}
