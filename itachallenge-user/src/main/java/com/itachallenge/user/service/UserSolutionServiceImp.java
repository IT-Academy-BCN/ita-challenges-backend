package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.*;
import com.itachallenge.user.enums.ChallengeStatus;
import com.itachallenge.user.exception.UnmodifiableSolutionException;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.UUID;


@Service
public class UserSolutionServiceImp implements IUserSolutionService {

    private static final Logger log = LoggerFactory.getLogger(UserSolutionServiceImp.class);
    private final IUserSolutionRepository userSolutionRepository;
    private final ConverterDocumentToDto converter;

    public UserSolutionServiceImp(IUserSolutionRepository userSolutionRepository, ConverterDocumentToDto converter) {
        this.userSolutionRepository = userSolutionRepository;
        this.converter = converter;
    }

    public Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String idUser, String idChallenge, String idLanguage) {
        UUID userUuid = UUID.fromString(idUser);
        UUID challengeUuid = UUID.fromString(idChallenge);
        UUID languageUuid = UUID.fromString(idLanguage);

        return this.userSolutionRepository.findByUserId(userUuid)
                .filter(userScore -> userScore.getChallengeId().equals(challengeUuid) && userScore.getLanguageId().equals(languageUuid))
                .flatMap(userScore -> converter.fromUserScoreDocumentToUserScoreDto(Flux.just(userScore)))
                .collectList()
                .map(userScoreDtos -> {
                    SolutionUserDto<UserScoreDto> solutionUserDto = new SolutionUserDto<>();
                    solutionUserDto.setInfo(0, 1, 0, userScoreDtos.toArray(new UserScoreDto[0]));
                    return solutionUserDto;
                });
    }


    @Override
    public Mono<UserSolutionScoreDto> addSolution(UserSolutionDto userSolutionDto) {
        UUID challengeUuid = UUID.fromString(userSolutionDto.getChallengeId());
        UUID languageUuid = UUID.fromString(userSolutionDto.getLanguageId());
        UUID userUuid = UUID.fromString(userSolutionDto.getUserId());
        String status = userSolutionDto.getStatus();
        ChallengeStatus challengeStatus;
        List<SolutionDocument> solutionDocuments;

        solutionDocuments = List.of(
                SolutionDocument.builder()
                        .uuid(UUID.randomUUID())
                        .solutionText(userSolutionDto.getSolutionText())
                        .build()
        );
        challengeStatus = determineChallengeStatus(status);

        if (challengeStatus == null) {
            log.error("POST operation failed due to invalid challenge status parameter");
            return Mono.error(new IllegalArgumentException("Status not allowed"));
        }
        return saveValidSolution(userUuid, challengeUuid, languageUuid, challengeStatus, solutionDocuments)
            .map(savedDocument -> UserSolutionScoreDto.builder()
                    .userId(String.valueOf(savedDocument.getUserId()))
                    .languageId(String.valueOf(savedDocument.getLanguageId()))
                    .challengeId(String.valueOf(savedDocument.getChallengeId()))
                    .solutionText(savedDocument.getSolutionDocument().get(0).getSolutionText())
                    .score(savedDocument.getScore())
                    .build())
            .doOnSuccess(userSolutionDocument -> log.info("Successfully POSTed solution"))
            .doOnError(error -> log.error("POST operation failed with error message: {}", error.getMessage()));
    }

    public Mono<UserSolutionDocument> markAsBookmarked(String uuidChallenge, String uuidLanguage, String uuidUser, boolean bookmarked) {
        UUID challengeId = UUID.fromString(uuidChallenge);
        UUID languageId = UUID.fromString(uuidLanguage);
        UUID userId = UUID.fromString(uuidUser);


        return userSolutionRepository
                .findByUserIdAndChallengeIdAndLanguageId(userId, challengeId, languageId)
                .flatMap(userSolutionDocument -> {
                    userSolutionDocument.setBookmarked(bookmarked);
                    return userSolutionRepository.save(userSolutionDocument).thenReturn(userSolutionDocument);
                })
                .switchIfEmpty(createAndSaveNewBookmark(challengeId, languageId, userId, bookmarked));
    }
    private Mono<UserSolutionDocument> createAndSaveNewBookmark(UUID challengeId, UUID languageId, UUID userId, boolean bookmarked) {
        UserSolutionDocument newDocument = UserSolutionDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId)
                .languageId(languageId)
                .bookmarked(bookmarked)
                .build();

        return userSolutionRepository.save(newDocument).thenReturn(newDocument);
    }

    private Mono<UserSolutionDocument> saveValidSolution(UUID userUuid, UUID challengeUuid, UUID languageUuid, ChallengeStatus challengeStatus, List<SolutionDocument> solutionDocuments) {
        return userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid)
                .flatMap(existingSolution -> {
                    if(existingSolution.getStatus().equals(ChallengeStatus.ENDED)) {
                        return Mono.error(new UnmodifiableSolutionException("Existing solution has status ENDED"));
                    }
                    existingSolution.setSolutionDocument(solutionDocuments);
                    existingSolution.setStatus(challengeStatus);
                    return userSolutionRepository.save(existingSolution);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    UserSolutionDocument userSolutionDocument = UserSolutionDocument.builder()
                            .uuid(UUID.randomUUID())
                            .userId(userUuid)
                            .challengeId(challengeUuid)
                            .languageId(languageUuid)
                            .status(challengeStatus)
                            .score(13)    // TODO GET SCORE FROM SCORE SERVICE
                            .solutionDocument(solutionDocuments)
                            .build();
                    return userSolutionRepository.save(userSolutionDocument);
                }));
    }

    private ChallengeStatus determineChallengeStatus(String status) {
        ChallengeStatus challengeStatus = null;

        if(status == null || status.isEmpty()) {
            challengeStatus = ChallengeStatus.STARTED;
        } else if (status.equalsIgnoreCase("ENDED")) {
            challengeStatus = ChallengeStatus.ENDED;
        }
        return challengeStatus;
    }

    @Override
    public Mono<ResponseEntity<UserSolutionDocument>> addScore(String idUser, String idChallenge, String idSolution) {  // phase 1 returns solToSend
        // public Mono<SolutionScoreDto> addScore(String idUser, String idChallenge, String idSolution) {   to do phase 2

        UUID uuidUser = UUID.fromString(idUser);
        UUID uuidChallenge = UUID.fromString(idChallenge);
        UUID uuidSolution = UUID.fromString(idSolution);

        Mono<UserSolutionDocument> solutionToSend =  userSolutionRepository.findByUserIdAndChallengeIdAndSolutionId(uuidUser, uuidChallenge, uuidSolution);

        return solutionToSend.map(scoreReq -> {
            UserSolutionScoreDto solToSend = new UserSolutionScoreDto();
            solToSend.setChallengeId(idChallenge);
            solToSend.setLanguageId(String.valueOf(scoreReq.getLanguageId()));
            solToSend.setSolutionText(scoreReq.getSolutionDocument().get(0).getSolutionText());
            solToSend.setScore(scoreReq.getScore()); // It will be 0 by default when solution is created
            return ResponseEntity.ok(scoreReq);
        });
    }
}
