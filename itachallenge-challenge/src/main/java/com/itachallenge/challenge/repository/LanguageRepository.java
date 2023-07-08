package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.LanguageDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;


public interface LanguageRepository extends ReactiveMongoRepository<LanguageDocument, Integer> {


    Mono<LanguageDocument> findByIdLanguage(int id);

    Mono<Void> deleteByIdLanguage(int id);
}
