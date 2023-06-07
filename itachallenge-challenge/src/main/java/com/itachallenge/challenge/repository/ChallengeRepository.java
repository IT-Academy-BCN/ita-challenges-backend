package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.Challenge;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveMongoRepository<Challenge, UUID> {
    @Query("{$pull: {resources: {id: ?0}}}")
    void removeResourceFromAllChallenges(String resourceId);
}
