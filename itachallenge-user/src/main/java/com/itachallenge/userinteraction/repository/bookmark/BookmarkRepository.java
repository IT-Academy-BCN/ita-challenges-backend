package com.itachallenge.userinteraction.repository.bookmark;

import com.itachallenge.userinteraction.document.bookmark.BookmarkDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BookmarkRepository extends ReactiveMongoRepository<BookmarkDocument, UUID> {
    Flux<BookmarkDocument> findByUserId(UUID userId);
    Mono<BookmarkDocument> findByUserIdAndChallengeId(UUID userId, UUID challengeId);
    Mono<Void> deleteByUserIdAndChallengeId(UUID userId, UUID challengeId);
    Mono<Boolean> existsByUserIdAndChallengeId(UUID userId, UUID challengeId);
}
