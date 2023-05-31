package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.documents.Language;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LanguageRepository extends ReactiveMongoRepository<Language, Integer> {


    Mono<Language> findByIdLanguage(int id);

    Mono<Void> deleteByIdLanguage(int id);
}
