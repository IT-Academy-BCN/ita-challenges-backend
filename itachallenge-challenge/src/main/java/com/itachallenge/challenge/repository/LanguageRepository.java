package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.Language;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;


public interface LanguageRepository extends ReactiveMongoRepository<Language, Integer> {


    Mono<Language> findByIdLanguage(int id);

    Mono<Void> deleteByIdLanguage(int id);
}
