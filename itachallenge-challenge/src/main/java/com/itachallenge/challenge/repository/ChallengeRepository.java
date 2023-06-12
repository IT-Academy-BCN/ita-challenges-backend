package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.documents.Challenge;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChallengeRepository extends ReactiveMongoRepository<Challenge, String> {

    Flux<Challenge> findAllByResourcesContaining(String idResource);
}
