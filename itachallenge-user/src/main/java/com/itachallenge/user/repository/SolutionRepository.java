package com.itachallenge.user.repository;

import com.itachallenge.user.document.Solutions;
import com.itachallenge.user.document.Status;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SolutionRepository extends ReactiveMongoRepository<Solutions, UUID> {

    Mono<Solutions> findByUuid(UUID uuid);
    Flux<Solutions> findByUserId(UUID userId);
    Flux<Solutions> findByChallengeId(UUID challengeId);
    Flux<Solutions> findByLanguageId(UUID languageId);
    Flux<Solutions> findAllByBookmarked(Boolean bookmarked);
    Flux<Solutions> findByStatus(String status);
    Mono<Boolean> existsByUuid(UUID uuid);
    Mono<Void> deleteByUuid(UUID uuid);

    @Override
    Mono<Solutions> save (Solutions solution);

}
