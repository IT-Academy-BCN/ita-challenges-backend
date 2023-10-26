package com.itachallenge.user.repository;

import com.itachallenge.user.document.UserSolutionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface IUserSolutionRepository extends ReactiveMongoRepository<UserSolutionDocument, UUID> {

    Flux<UserSolutionDocument> findByUserId(UUID userId);

}
