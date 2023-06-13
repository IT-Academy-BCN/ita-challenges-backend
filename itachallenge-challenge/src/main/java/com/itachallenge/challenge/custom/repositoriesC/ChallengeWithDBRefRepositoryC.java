package com.itachallenge.challenge.custom.repositoriesC;

import com.itachallenge.challenge.custom.documentsC.ChallengeDocWithDBRefC;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository //not needed, but clarifies... TODO: remove annotation
public interface ChallengeWithDBRefRepositoryC extends ReactiveMongoRepository<ChallengeDocWithDBRefC, UUID> {
}
