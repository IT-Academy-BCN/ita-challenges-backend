package com.itachallenge.submission.repository;

import com.itachallenge.submission.document.SubmissionDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

@DataMongoTest
class SubmissionRepositoryTest {

    @Autowired
    private SubmissionRepository submissionRepository;

    private UUID userId;
    private UUID otherUserId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        SubmissionDocument s1 = SubmissionDocument.builder()
                .submissionId(UUID.randomUUID())
                .userId(userId)
                .build();

        SubmissionDocument s2 = SubmissionDocument.builder()
                .submissionId(UUID.randomUUID())
                .userId(userId)
                .build();

        SubmissionDocument s3 = SubmissionDocument.builder()
                .submissionId(UUID.randomUUID())
                .userId(otherUserId)
                .build();

        submissionRepository.deleteAll()
                .thenMany(submissionRepository.saveAll(List.of(s1, s2, s3)))
                .blockLast();
    }

    @Test
    void shouldFindAllSubmissionsByUserId() {
        StepVerifier.create(submissionRepository.findAllByUserId(userId))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoSubmissions() {
        StepVerifier.create(
                        submissionRepository.findAllByUserId(UUID.randomUUID())
                )
                .verifyComplete();
    }
}
