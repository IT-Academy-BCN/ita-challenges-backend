package com.itachallenge.challenge.custom.repositoriesC;

import com.itachallenge.challenge.custom.documentsC.LanguageDocC;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository //not needed, but clarifies... TODO: remove annotation
public interface LanguageRepositoryC extends ReactiveMongoRepository<LanguageDocC, Integer> {
}
