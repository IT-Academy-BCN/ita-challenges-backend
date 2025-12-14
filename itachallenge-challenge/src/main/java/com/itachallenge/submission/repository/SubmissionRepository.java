package com.itachallenge.submission.repository;

import com.itachallenge.submission.document.SubmissionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface SubmissionRepository extends ReactiveMongoRepository<SubmissionDocument, UUID> {

    Flux<SubmissionDocument> findAllByUserId(UUID userId);
}
