package com.itachallenge.submission.repository;

import com.itachallenge.submission.document.SubmissionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ISubmissionRepository extends ReactiveMongoRepository<SubmissionDocument, UUID> {

    Mono<SubmissionDocument> findByUserIdAndChallengeIdAndLanguageId(UUID userId, UUID challengeId, UUID languageId);
    Flux<SubmissionDocument> findAllByUserId(UUID userId);
}
