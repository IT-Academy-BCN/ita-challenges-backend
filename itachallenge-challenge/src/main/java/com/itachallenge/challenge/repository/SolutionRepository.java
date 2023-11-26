package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.Locale;
import com.itachallenge.challenge.document.SolutionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SolutionRepository extends ReactiveMongoRepository<SolutionDocument, UUID> {

    Mono<Boolean> existsByUuidAndLocale(UUID uuid, Locale locale);

    Mono<SolutionDocument> findByUuidAndLocale(UUID uuid, Locale locale);

    Mono<Void> deleteByUuidAndLocale(UUID uuid, Locale locale);
}
