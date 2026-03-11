package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserScoreRepository extends ReactiveMongoRepository<UserScoreDocument, UUID> {

    Flux<UserScoreDocument> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Aggregates user scores to create a leaderboard.
     *
     * For each user, sums all points_earned and returns the most recent username.
     * Results are sorted by totalPoints in descending order.
     *
     * @param skip number of records to skip (for pagination)
     * @param limit maximum number of records to return
     * @return Flux of aggregated user scores with userId, username, and totalPoints
     */
    @Aggregation(pipeline = {
            "{$sort: {user_id: 1, created_at: -1}}",
            "{$group: {_id: '$user_id', " +
                    "username: {$first: '$username'}, " +
                    "totalPoints: {$sum: '$points_earned'}}}",
            "{$addFields: {user_id: '$_id'}}",
            "{$project: {_id: 0, user_id: 1, " +
                    "username: 1, " +
                    "totalPoints: 1}}",
            "{$sort: {totalPoints: -1}}",
            "{$skip: ?0}",
            "{$limit: ?1}"
    })
    Flux<UserScoreAggregation> aggregateUserScores(int skip, int limit);

    /**
     * Counts the total number of distinct users who have scores.
     *
     * @return Mono containing the count of distinct users
     */
    @Aggregation(pipeline = {
            "{$group: {_id: '$user_id'}}",
            "{$count: 'total'}"
    })
    Mono<Long> countDistinctUsers();
}
