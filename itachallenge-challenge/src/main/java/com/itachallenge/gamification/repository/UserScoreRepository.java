package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface UserScoreRepository extends ReactiveMongoRepository<UserScoreDocument, UUID> {

    Flux<UserScoreDocument> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Aggregation(pipeline = {
            "{$sort: {user_id: 1, created_at: -1}}",
            "{$group: {_id: '$user_id', " +
                    "username: {$first: '$username'}, " +
                    "totalPoints: {$sum: '$points_earned'}}}",
            "{$project: {_id: 0, username: 1, totalPoints: 1}}",
            "{$sort: {totalPoints: -1}}"
    })
    Flux<UserScoreAggregation> aggregateUserScores();
}
