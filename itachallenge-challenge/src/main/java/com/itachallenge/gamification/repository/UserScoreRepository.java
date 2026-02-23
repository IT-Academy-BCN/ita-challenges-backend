package com.itachallenge.gamification.repository;

import com.itachallenge.gamification.document.UserScoreDocument;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface UserScoreRepository extends ReactiveMongoRepository<UserScoreDocument, UUID> {

    Flux<UserScoreDocument> findByUserId(UUID userId, Pageable pageable);
    Flux<UserScoreDocument> findByUserIdOrderByCreatedAtAsc(UUID userId);
}
