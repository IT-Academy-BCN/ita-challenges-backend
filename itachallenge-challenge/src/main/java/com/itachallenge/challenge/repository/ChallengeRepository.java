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
     * Filtra la búsqueda en el campo languages
     * @param language filtra la búsqueda dentro del campo languages con un valor específico como el nombre del lenguaje
     * @param level Filtra la búsqueda en el campo level
     * @return
     */
    Flux<ChallengeDocument> findByLanguages_LanguageNameInOrLevelIn(Set<String> language, Set<String> level);

    @Override
    Mono<ChallengeDocument> save (ChallengeDocument challenge);

}
