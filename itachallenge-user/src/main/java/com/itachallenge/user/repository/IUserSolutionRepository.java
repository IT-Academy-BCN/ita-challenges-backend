package com.itachallenge.user.repository;

import com.itachallenge.user.document.UserSolutionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IUserSolutionRepository extends ReactiveMongoRepository<UserSolutionDocument, UUID> {

    Flux<UserSolutionDocument> findByUserId(UUID userId);
    Mono<UserSolutionDocument> findByChallengeIdAndLanguajeIdAndUserId(UUID challengeId, UUID languageId, UUID userId);


}
