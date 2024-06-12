package com.itachallenge.challenge.repository;


import com.itachallenge.challenge.document.ChallengeDocument;

import com.itachallenge.challenge.document.SolutionDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.mongodb.repository.Query;

import java.util.UUID;



@Repository
public interface ChallengeRepository extends ReactiveSortingRepository<ChallengeDocument, UUID> {

    Mono<Boolean> existsByUuid(UUID uuid);
    Mono<ChallengeDocument> findByUuid(UUID uuid);
    Flux<ChallengeDocument> findByLevel(String level);
    @Query(value = "{}", fields = "{'testingValues':0}")
    Flux<ChallengeDocument> findAllByUuidNotNullExcludingTestingValues();
    Flux<ChallengeDocument> findAllByResourcesContaining(UUID idResource);
    Mono<Void> deleteByUuid(UUID uuid);
    Mono<ChallengeDocument> save(ChallengeDocument challenge);
    Flux<ChallengeDocument> saveAll(Flux<ChallengeDocument> challengeDocumentFlux);
    Flux<ChallengeDocument> findByLevelAndLanguages_IdLanguage(String level, UUID idLanguage);
    Flux<ChallengeDocument> findByLanguages_IdLanguage(UUID idLanguage);
    Flux<ChallengeDocument> findByLanguages_LanguageName(String languageName);



    @Aggregation(pipeline = {
            "{ $lookup: { from: 'solutions', localField: 'solutions', foreignField: '_id', as: 'solutionData' } }",
            "{ $unwind: '$solutionData' }",
            "{ $match: { '_id': ?0, 'solutionData.language': ?1 } }",
            "{ $project: { _id: 0, 'uuid': '$solutionData._id', 'solution_text': '$solutionData.solution_text', 'language': '$solutionData.language', 'idChallenge': '$_id' } }"
    })
    Flux<SolutionDocument> aggregateChallengesWithSolutions(UUID challengeID, UUID lenguageID);



}
