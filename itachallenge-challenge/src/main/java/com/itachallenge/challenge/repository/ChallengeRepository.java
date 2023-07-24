package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.ChallengeDocument;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveMongoRepository<ChallengeDocument,UUID> {

    Mono<Boolean> existsByUuid(UUID uuid);

    Mono<ChallengeDocument> findByUuid(UUID uuid);

    Mono<Void> deleteByUuid(UUID uuid);

    Mono<ChallengeDocument> findByLevel(String level);

    Mono<ChallengeDocument> findByTitle(String title);

    Flux<ChallengeDocument> findAllByResourcesContaining(UUID idResource);

    @Override
    Mono<ChallengeDocument> save (ChallengeDocument challenge);

    @Query("{().skip((pageNumber - 1) * pageSize).limit(pageSize)}") default
    Flux<ChallengeDocument> findChallengesPaginated() {
        return findChallengesPaginated(1, 1);
    }

    @Query("{().skip((pageNumber - 1) * pageSize).limit(pageSize)}")
    Flux<ChallengeDocument> findChallengesPaginated(int pageNumber, int pageSize);

}
