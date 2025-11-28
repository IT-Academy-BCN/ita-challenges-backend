package com.itachallenge.submission.repository;

import com.itachallenge.submission.document.UserSubmissionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface IUserSubmissionRepository extends ReactiveMongoRepository<UserSubmissionDocument, UUID> {

    Mono<UserSubmissionDocument> findByUserIdAndChallengeIdAndLanguageId(UUID userId, UUID challengeId, UUID languageId);
    Flux<UserSubmissionDocument> findAllByUserId(UUID userId);
}
