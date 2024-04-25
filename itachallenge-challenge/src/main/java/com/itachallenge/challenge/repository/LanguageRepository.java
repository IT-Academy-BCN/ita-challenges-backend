package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.LanguageDocument;
import com.mongodb.lang.NonNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface LanguageRepository extends ReactiveMongoRepository<LanguageDocument, UUID> {

    Mono<LanguageDocument> findByIdLanguage(UUID id);

    Mono<Void> deleteByIdLanguage(UUID id);
}
