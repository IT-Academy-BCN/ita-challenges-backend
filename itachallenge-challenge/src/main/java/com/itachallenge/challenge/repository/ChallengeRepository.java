package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.ChallengeDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveMongoRepository<ChallengeDocument,UUID> {

    Mono<Boolean> existsByIdChallenge(UUID idChallenge);

    Mono<ChallengeDocument> findByIdChallenge(UUID idChallenge);

    Mono<Void> deleteByIdChallenge(UUID idChallenge);

    Mono<ChallengeDocument> findByLevel(String level);

    Mono<ChallengeDocument> findByTitle(String title);

    @Override
    Mono<ChallengeDocument> save (ChallengeDocument challenge);

}
