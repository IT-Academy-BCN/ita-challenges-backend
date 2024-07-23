package com.itachallenge.user.repository;


import com.itachallenge.user.document.SolutionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface IUserScoreRepository extends ReactiveMongoRepository<SolutionDocument, UUID> {
    Mono<SolutionDocument> findByUuid(UUID uuid);
}
