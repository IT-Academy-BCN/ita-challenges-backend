package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.Challenge;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveMongoRepository<Challenge, UUID> {


}
