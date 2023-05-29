package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.Language;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguagesRepository extends ReactiveMongoRepository<Language, Integer> {
}
