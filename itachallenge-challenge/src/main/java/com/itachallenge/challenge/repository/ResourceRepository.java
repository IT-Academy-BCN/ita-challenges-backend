package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.ResourceDocument;
import com.itachallenge.challenge.enums.ResourceContentType;
import com.itachallenge.challenge.enums.Topic;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ResourceRepository extends ReactiveSortingRepository<ResourceDocument, UUID> {

    Mono<Boolean> existsByResourceId(UUID uuid);
    Mono<ResourceDocument> findByResourceId(UUID uuid);
    Flux<ResourceDocument> findByTopic(Topic topic);
    Flux<ResourceDocument> findByContentType(ResourceContentType contentType);


    Mono<Long> count();
    Mono<Void> deleteByResourceId(UUID uuid);
    Mono<ResourceDocument> save(ResourceDocument resource);
    Flux<ResourceDocument> saveAll(Flux<ResourceDocument> resourceDocumentFlux);
    Mono<Void> deleteAll();

    Flux<ResourceDocument> findByChallengeIdsContaining(UUID challengeId);
}
