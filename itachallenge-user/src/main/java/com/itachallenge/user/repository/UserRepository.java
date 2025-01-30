package com.itachallenge.user.repository;

import com.itachallenge.user.document.UserDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveMongoRepository<UserDocument, UUID> {

    Mono<UserDocument> findByUuid(UUID uuid);
    Mono<UserDocument> findByUsername(String username);
    Mono<Boolean> existsByUuid(UUID uuid);
}
