package com.itachallenge.score.repository;

import com.itachallenge.score.document.Score;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IScoreRepository extends ReactiveMongoRepository<Score, UUID> {
    Mono<Score> findByScoreID(UUID scoreID);
    Flux<Score> findAllByUserID(UUID userID);
    Mono<Boolean> existsByScoreID(UUID scoreID);
    Mono<Boolean> existsByUserID(UUID userID);
    @Override
    Mono<Score> save (Score score);

}
