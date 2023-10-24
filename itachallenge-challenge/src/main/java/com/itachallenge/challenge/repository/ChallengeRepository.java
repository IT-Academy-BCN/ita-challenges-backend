package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.ChallengeDocument;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ChallengeRepository extends ReactiveMongoRepository<ChallengeDocument,UUID> {

    Mono<Boolean> existsByUuid(UUID uuid);

    Mono<ChallengeDocument> findByUuid(UUID uuid);

    Mono<Void> deleteByUuid(UUID uuid);

    Mono<ChallengeDocument> findByLevel(String level);

    Mono<ChallengeDocument> findByTitle(String title);

    Flux<ChallengeDocument> findAllByResourcesContaining(UUID idResource);

    Flux<ChallengeDocument> findByLevelAndLanguages_IdLanguage(String Level, UUID idLanguage);

//    @Query("SELECT * FROM ChallengeDocument c " +
//            "JOIN c.languages l " +
//            "WHERE c.level = :level AND l.idLanguage = :idLanguage")
//    Flux<ChallengeDocument> findChallengesByLevelAndLanguage2(
//            @Param("level") String level,
//            @Param("idLanguage") UUID idLanguage
//    );
//    @Query("SELECT * FROM ChallengeDocument c" +
//            "JOIN LanguageDocument l ON c.languages.language_id = l.idLanguage" +
//            "WHERE c.level = :level AND l.idLanguage = :idLanguage")
//    Flux<ChallengeDocument> findChallengesByLevelAndLanguage2(
//            @Param("level") String level,
//            @Param("idLanguage") UUID idLanguage
//    );

    @Query("{'level' : ?0, 'languages.idLanguage' : ?1}")
    Flux<ChallengeDocument> findChallengesByLevelAndLanguage(String level, UUID idLanguage);

    Flux<ChallengeDocument> findByLanguages_IdLanguageIn(UUID idLanguage);

    @Query("{'languages.idLanguage' : ?0}")
    Flux<ChallengeDocument> findChallengesByLanguages(UUID idLanguage);


    @Override
    Mono<ChallengeDocument> save (ChallengeDocument challenge);

}
