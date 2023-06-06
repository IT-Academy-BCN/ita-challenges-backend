package com.itachallenge.score.repository;

import com.itachallenge.score.document.Score;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.UUID;

public interface IScoreRepository extends ReactiveMongoRepository<Score, UUID> {
}
