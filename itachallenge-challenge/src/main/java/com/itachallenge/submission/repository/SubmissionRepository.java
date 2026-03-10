package com.itachallenge.submission.repository;

import com.itachallenge.submission.document.SubmissionDocument;
import com.itachallenge.submission.enums.SubmissionStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends ReactiveMongoRepository<SubmissionDocument, UUID> {

    Flux<SubmissionDocument> findAllByUserId(UUID userId);

    Mono<SubmissionDocument> findByUserIdAndChallengeIdAndLanguageId(UUID userId, UUID challengeId, UUID languageId);

    Mono<Boolean> existsByUserIdAndChallengeIdAndStatusIn(UUID userId, UUID challengeId, List<SubmissionStatus> statuses);

    Flux<SubmissionDocument> findTop10ByChallengeIdAndUserIdNotAndStatusInOrderByCreatedAtDesc(
            UUID challengeId, UUID userId, List<SubmissionStatus> statuses);
}
