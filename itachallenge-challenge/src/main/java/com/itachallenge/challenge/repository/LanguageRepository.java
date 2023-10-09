package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.LanguageDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface LanguageRepository extends ReactiveMongoRepository<LanguageDocument, UUID> {


    Mono<LanguageDocument> findByLanguageId(UUID id);

    Mono<Void> deleteByLanguageId(UUID id);
}
