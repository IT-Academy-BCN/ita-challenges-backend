package com.itachallenge.user.repository;

import com.itachallenge.user.document.UserSolutions;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface IUserSolutionsRepository extends ReactiveMongoRepository<UserSolutions, UUID> {

    Mono<UserSolutions> findByUuid(UUID uuid);
    Flux<UserSolutions> findByUserId(UUID userId);
    Flux<UserSolutions> findByChallengeId(UUID challengeId);
    Flux<UserSolutions> findByLanguageId(UUID languageId);
    Flux<UserSolutions> findByBookmarked(Boolean bookmarked);
    Flux<UserSolutions> findByScore(int score);
    Flux<UserSolutions> findByStatus(String status);
    Mono<Boolean> existsByUuid(UUID uuid);

    @Override
    Mono<UserSolutions> save (UserSolutions solutions);

}