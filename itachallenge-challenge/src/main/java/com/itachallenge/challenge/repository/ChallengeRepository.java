package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.Challenge;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveMongoRepository<Challenge, String> {

    Mono<Boolean> existsByUuid(String uuid);

    Mono<Challenge> findByUuid(String uuid);

    Mono<Void> deleteByUuid(String uuid);

    Mono<Challenge> findByLevel(String level);

    Mono<Challenge> findByTitle(String title);

    @Override
    Mono<Challenge> save (Challenge challenge);
}
