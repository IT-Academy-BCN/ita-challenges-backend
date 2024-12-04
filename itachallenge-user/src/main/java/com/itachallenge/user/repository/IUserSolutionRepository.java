package com.itachallenge.user.repository;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.enums.ChallengeStatus;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public interface IUserSolutionRepository extends ReactiveMongoRepository<UserSolutionDocument, UUID> {

    Mono<UserSolutionDocument> findByUuid(UUID uuid);
    Flux<UserSolutionDocument> findByUserId(UUID userId);
    Flux<UserSolutionDocument> findByChallengeId(UUID challengeId);

    Flux<UserSolutionDocument> findByUserIdAndChallengeId(UUID userId, UUID challengeId);
    Mono<UserSolutionDocument> findByUserIdAndChallengeIdAndLanguageId(UUID userId, UUID challengeId, UUID languageId);
    Flux<UserSolutionDocument> findByLanguageId(UUID languageId);
    Flux<UserSolutionDocument> findByBookmarked(Boolean bookmarked);
    Flux<UserSolutionDocument> findByScore(int score);
    Flux<UserSolutionDocument> findByStatus(ChallengeStatus status);
    Flux<UserSolutionDocument> findByChallengeIdAndStatus(UUID challengeId, ChallengeStatus status);
    Mono<Boolean> existsByUuid(UUID uuid);
    Mono<Long> countByChallengeIdAndBookmarked(UUID challengeId, boolean isBookmarked);
    @Query ("{'solutions.userId' : ?0, 'solutions.languages.idLanguage' : ?1, 'solutions.status': {$in: ?2}" +
            " 'solutions.score': {$gte: 75}}")
    Mono<Long> countByChallengeStatusAndLanguageAndScoreAmount (UUID idUser, UUID idLanguage, List<ChallengeStatus> status);

    //    @Query("{'solutions': {$elemMatch: {'userId': ?1, 'idLanguage': ?2, 'status': {$in: ?3}}}}")
//    @Query("{'solutions.userId' : ?1, 'solutions.languageId' : ?2, 'solutions.status' : {$in: ?3}}")
    @Query("{'solutions.userId' : :#{#userId}, 'solutions.languageId' : :#{#idLanguage}, 'solutions.status' : {$in: :#{#statuses}}}")
    Mono<Long> countChallengesByStatusAndLanguage (UUID userId, UUID idLanguage, List<ChallengeStatus> statuses);

//    @Query("{'solutions.userId' : ?1, 'solutions.languageId' : ?2, 'solutions.status' : ?3}")
    @Query(value = "{'solutions.userId' : ?0 }", count = true)

//    @Query("{'lastname': ?#{[0]} }")
//    @Query("{'solutions.userId' : ?#{[0]}") //, 'solutions.languageId' : ?#{[1]}, 'solutions.status' : ?#{[2]}}")
    Mono<Long> countChallengesByStatusAndLanguage2 (UUID userId);    //), UUID idLanguage, ChallengeStatus status);
//    Mono<Long> countChallengesByStatusAndLanguage2(@Param("userId") UUID userId, @Param("idLanguage") UUID idLanguage, @Param("status") ChallengeStatus status);

}
