package com.itachallenge.gamification.repository;

import com.itachallenge.challenge.dto.gamification.RankingResponseDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface UserScoreRepository extends ReactiveMongoRepository<UserScoreDocument, UUID> {

    Flux<UserScoreDocument> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Aggregation(pipeline = {
            "{ $group: { _id: '$user_id', username: { $last: '$username' }, points: { $sum: '$points_earned' } } }",
            "{ $sort: { points: -1 } }",
            "{ $project: { username: 1, points: 1, _id: 0 } }"
    })
    Flux<RankingResponseDto> findUsersRanking();

}
