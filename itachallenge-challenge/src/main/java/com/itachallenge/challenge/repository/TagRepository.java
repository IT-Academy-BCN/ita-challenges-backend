package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.TagDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TagRepository extends ReactiveMongoRepository<TagDocument, UUID> {

    Mono<TagDocument> findByTagName(String tagName);

    Mono<Void> deleteByTagName(String tagName);

}
