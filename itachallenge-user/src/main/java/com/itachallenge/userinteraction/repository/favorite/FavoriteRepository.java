package com.itachallenge.userinteraction.repository.favorite;

import com.itachallenge.userinteraction.document.favorite.FavoriteDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends ReactiveMongoRepository<FavoriteDocument, UUID> {
    Flux<FavoriteDocument> findByUserId(UUID userId);
    Mono<FavoriteDocument> findByUserIdAndChallengeId(UUID userId, UUID challengeId);
    Mono<Void> deleteByUserIdAndChallengeId(UUID userId, UUID challengeId);
    Mono<Boolean> existsByUserIdAndChallengeId(UUID userId, UUID challengeId);

}
