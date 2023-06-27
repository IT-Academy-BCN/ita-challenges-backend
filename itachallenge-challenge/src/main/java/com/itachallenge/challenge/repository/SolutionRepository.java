package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.SolutionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SolutionRepository extends ReactiveMongoRepository<SolutionDocument, UUID> {

    Mono<Boolean> existsByUuid(UUID uuid);

    Mono<SolutionDocument> findByUuid(UUID uuid);

    Mono<Void> deleteByUuid(UUID uuid);
}
