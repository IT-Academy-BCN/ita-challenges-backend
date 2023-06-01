package com.itachallenge.score.repository;

import com.itachallenge.score.entity.Score;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.UUID;

public interface ScoreRepository extends ReactiveMongoRepository<Score, UUID> {
}
