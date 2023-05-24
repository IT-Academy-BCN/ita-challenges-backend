package com.itachallenge.challenge.repositories;

import com.itachallenge.challenge.documents.Challenge;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveMongoRepository<Challenge, UUID> {


}
