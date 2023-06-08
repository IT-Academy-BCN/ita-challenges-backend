package com.itachallenge.user.repository;

import com.itachallenge.user.document.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface IUserRepository extends ReactiveMongoRepository<User, UUID> {

    Mono<User> findByUuid(UUID uuid);
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByUuid(UUID uuid);
    Mono<Boolean> existsByEmail(String email);
    @Override
    Mono<User> save (User user);

}
