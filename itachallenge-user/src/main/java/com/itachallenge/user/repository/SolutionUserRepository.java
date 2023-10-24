package com.itachallenge.user.repository;

import com.itachallenge.user.document.SolutionUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SolutionUserRepository extends ReactiveMongoRepository<SolutionUser, UUID> {
    @Override
    Mono<SolutionUser> save (SolutionUser solutionUser);
}
