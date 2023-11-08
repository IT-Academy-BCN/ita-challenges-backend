package com.itachallenge.user.repository;

import com.itachallenge.user.document.UserSolutionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface IUserSolutionRepository extends ReactiveMongoRepository<UserSolutionDocument, UUID> {

    Mono<UserSolutionDocument> findByUuid(UUID uuid);
    Flux<UserSolutionDocument> findByUserId(UUID userId);
    Flux<UserSolutionDocument> findByChallengeId(UUID challengeId);
    Flux<UserSolutionDocument> findByLanguageId(UUID languageId);
    Flux<UserSolutionDocument> findByBookmarked(Boolean bookmarked);
    Flux<UserSolutionDocument> findByScore(int score);
    Flux<UserSolutionDocument> findByStatus(String status);
    Mono<Boolean> existsByUuid(UUID uuid);

    @Override
    Mono<UserSolutionDocument> save (UserSolutionDocument solutions);

}
