package com.itachallenge.user.repository;

import com.itachallenge.user.document.Solutions;
import com.itachallenge.user.document.Status;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ISolutionRepository extends ReactiveMongoRepository<Solutions, UUID> {

    Mono<Solutions> findByUUID(UUID uuid);
    Flux<Solutions> findByUser(UUID userId);
    Flux<Solutions> findByChallenge(UUID challengeId);
    Flux<Solutions> findByLanguage(UUID languageId);
    Flux<Solutions> findAllBookmarked(Boolean bookmarked);
    Flux<Solutions> findByStatus(Status status);
    Mono<Boolean> existsByUUID(UUID uuid);
    Mono<Void> deleteByUUID(UUID uuid);

    @Override
    Mono<Solutions> save (Solutions solution);

}
