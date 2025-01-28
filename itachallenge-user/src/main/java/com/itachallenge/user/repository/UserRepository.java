package com.itachallenge.user.repository;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.enums.ChallengeStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveMongoRepository<UserSolutionDocument, UUID> {

    Mono<UserSolutionDocument> findByUuid(UUID uuid);
    Flux<UserSolutionDocument> findByUserId(UUID userId);
    Mono<Boolean> existsByUuid(UUID uuid);
}
