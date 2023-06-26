package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.Challenge;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ChallengeRepository extends ReactiveMongoRepository<Challenge,UUID> {

    Mono<Boolean> existsByUuid(UUID uuid);

    Mono<Challenge> findByUuid(UUID uuid);

    Mono<Void> deleteByUuid(UUID uuid);

    Mono<Challenge> findByLevel(String level);

    Mono<Challenge> findByTitle(String title);

    @Override
    Mono<Challenge> save (Challenge challenge);



}
