package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.Solution;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SolutionRepository extends ReactiveMongoRepository<Solution, UUID> {

    Mono<Boolean> existsByUuid(UUID uuid);

    Mono<Solution> findByUuid(UUID uuid);

    Mono<Void> deleteByUuid(UUID uuid);
}
