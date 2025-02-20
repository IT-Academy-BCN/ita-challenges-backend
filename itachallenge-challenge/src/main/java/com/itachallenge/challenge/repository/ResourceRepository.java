package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.ResourceDocument;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ResourceRepository extends ReactiveSortingRepository<ResourceDocument, UUID> {

    Mono<Boolean> existsByResourceId(UUID uuid);
    Mono<ResourceDocument> findByResourceId(UUID uuid);
    Flux<ResourceDocument> findByTopic(String topic);
    Flux<ResourceDocument> findByContentType(String contentType);

    @Query(value = "{}")
    Flux<ResourceDocument> findAllByUuidNotNull();
    Mono<Long> count();
    Mono<Void> deleteByResourceId(UUID uuid);
    Mono<ResourceDocument> save(ResourceDocument resource);
    Flux<ResourceDocument> saveAll(Flux<ResourceDocument> resourceDocumentFlux);

    @Query(value = "{ 'topic' : ?0, 'challenge_ids' : ?1 }")
    Flux<ResourceDocument> findByTopicAndChallengeId(String topic, UUID challengeId);

    @Query(value = "{ 'challenge_ids' : ?0 }")
    Flux<ResourceDocument> findByChallengeId(UUID challengeId);
}
