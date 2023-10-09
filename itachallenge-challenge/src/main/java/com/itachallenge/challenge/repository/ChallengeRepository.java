package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.ChallengeDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveMongoRepository<ChallengeDocument,UUID> {

    Mono<Boolean> existsByChallengeId(UUID uuid);

    Mono<ChallengeDocument> findByChallengeId(UUID uuid);

    Mono<Void> deleteByChallengeId(UUID uuid);

    Mono<ChallengeDocument> findByChallengeLevel(String level);

    Mono<ChallengeDocument> findByChallengeTitle(String title);
    Flux<ChallengeDocument> findAllByChallengeResourcesContaining(UUID idResource);

    @Override
    Mono<ChallengeDocument> save (ChallengeDocument challenge);

}
