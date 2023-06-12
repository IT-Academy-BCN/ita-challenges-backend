package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.Challenge;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveMongoRepository<Challenge, UUID> {

    Mono<Challenge> findFirstByUuid(UUID uuid);
    Mono<Challenge> findByUuid(UUID uuid);
    Mono<Challenge> findByTitle(UUID title);

    /*@Query(value = "db.challenges.find({ 'languages.idLanguage' : ?0 })")
    Flux<Challenge> findByLanguages(int idLanguage);*/

    @Override
    Mono<Challenge> save (Challenge challenge);
}
