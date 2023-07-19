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

    Flux<ChallengeDocument> findByLanguages_LanguageNameInAndLevelIn(Set<String> language, Set<String> level);
    //findByLanguages: filtra la búsqueda en el campo languages
    //_LanguageNameIn: filtra la búsqueda dentro del campo languages con un valor específico como el nombre del lenguaje
    //And: añade otro filtro adicional
    //Level: filtra la búsqueda en el campo level

    @Override
    Mono<ChallengeDocument> save (ChallengeDocument challenge);

}
