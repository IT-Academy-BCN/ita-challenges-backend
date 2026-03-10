package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserScoreRepository extends ReactiveMongoRepository<UserScoreDocument, UUID> {

    Flux<UserScoreDocument> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Aggregation(pipeline = {
            "{$group: {_id: '$user_id', username: {$last: '$username'}, totalPoints: {$sum: '$points_earned'}}}",
            "{$sort: {totalPoints: -1}}",
            "{$skip: ?0}",
            "{$limit: ?1}"
    })
    Flux<UserScoreAggregation> aggregateUserScores(int skip, int limit);

    @Aggregation(pipeline = {
            "{$group: {_id: '$userId'}}",
            "{$count: 'total'}"
    })
    Mono<Long> countDistinctUsers();
}
