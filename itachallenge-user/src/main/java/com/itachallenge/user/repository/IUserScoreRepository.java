package com.itachallenge.user.repository;

import com.itachallenge.user.document.UserScoreDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface IUserScoreRepository extends ReactiveMongoRepository<UserScoreDocument, UUID> {

    Flux<UserScoreDocument> findByUserId(String userId);

}
