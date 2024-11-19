package com.itachallenge.user.repository;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.enums.ChallengeStatus;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
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

//    @Query ("{'solutions.userId' : ?1, 'solutions.idLanguage' : ?2, 'solutions.status' : ?3}")
//    Mono<Long> countChallengesByStatusAndLanguage (UUID userId, UUID idLanguage, ChallengeStatus status);
    @Query ("{'solutions.userId' : ?1, 'solutions.idLanguage' : ?2, 'solutions.status' : {$in: [?3, ?4]}}")
    Mono<Long> countChallengesByStatusInTwoAndLanguage (UUID userId, UUID idLanguage, ChallengeStatus status1,
                                                        ChallengeStatus status2);
    @Query("{'solutions.userId': ?1, 'solutions.idLanguage': ?2, 'solutions.status': {$in: [?3, ?4, ?5]}}")
    Mono<Long> countChallengesByStatusInThreeAndLanguage (UUID userId, UUID idLanguage, ChallengeStatus status1,
                                                          ChallengeStatus status2, ChallengeStatus status3);

    @Query ("{'solutions.userId' : ?1, 'solutions.languages.idLanguage' : ?2, 'solutions.status': {$in: ?3}" +
            " 'solutions.score': {$gte: 75}}")
    Mono<Long> countByChallengeStatusAndLanguageAndScoreAmount (UUID idUser, UUID idLanguage, List<ChallengeStatus> status);

    @Query("{'solutions.userId' : ?1, 'solutions.idLanguage' : ?2, 'solutions.status' : {$in: ?3}}")
    Mono<Long> countChallengesByStatusAndLanguage (UUID userId, UUID idLanguage, List<ChallengeStatus> statuses);

}
