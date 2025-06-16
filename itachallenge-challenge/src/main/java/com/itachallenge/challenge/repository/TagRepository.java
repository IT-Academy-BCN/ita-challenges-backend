package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.TagDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface TagRepository extends ReactiveMongoRepository<TagDocument, UUID> {
    Flux<TagDocument> findByIdLanguage(UUID idLanguage);
}
