package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.SolutionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SolutionRepository extends ReactiveMongoRepository<SolutionDocument, UUID> {

    Mono<Boolean> existsByIdSolution(UUID idSolution);

    Mono<SolutionDocument> findByIdSolution(UUID idSolution);

    Mono<Void> deleteByIdSolution(UUID idSolution);
}
