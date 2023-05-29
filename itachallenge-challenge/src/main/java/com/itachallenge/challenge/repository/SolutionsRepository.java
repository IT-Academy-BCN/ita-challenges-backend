package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.Solution;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SolutionsRepository extends ReactiveMongoRepository<Solution, UUID> {

}
