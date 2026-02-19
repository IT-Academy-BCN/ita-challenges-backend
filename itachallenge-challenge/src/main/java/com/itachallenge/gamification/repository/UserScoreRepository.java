package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface UserScoreRepository extends ReactiveMongoRepository<UserScoreDocument, UUID> {

    Flux<UserScoreDocument> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
