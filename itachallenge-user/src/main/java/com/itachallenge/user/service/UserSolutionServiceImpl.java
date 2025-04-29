package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionAttemptDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dto.*;
import com.itachallenge.user.document.enums.ChallengeStatus;
import com.itachallenge.user.exception.UnmodificableSolutionException;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
public class UserSolutionServiceImpl implements IUserSolutionService {

    private static final Logger log = LoggerFactory.getLogger(UserSolutionServiceImpl.class);
    private final IUserSolutionRepository userSolutionRepository;

    public UserSolutionServiceImpl(IUserSolutionRepository userSolutionRepository) {
        this.userSolutionRepository = userSolutionRepository;
    }

    @Override
    public Mono<UserSolutionResponseDto> addSolution(UserSolutionRequestDto userSolutionDto) {
        UUID challengeUuid = UUID.fromString(userSolutionDto.getChallengeId());
        UUID languageUuid = UUID.fromString(userSolutionDto.getLanguageId());
        UUID userUuid = UUID.fromString(userSolutionDto.getUserId());
        String status = userSolutionDto.getStatus();
        ChallengeStatus challengeStatus;

        SolutionAttemptDocument solutionAttempt = SolutionAttemptDocument.builder()
                .uuid(UUID.randomUUID())
                .solutionText(userSolutionDto.getSolutionText())
                .build();

        challengeStatus = ChallengeStatus.determineChallengeStatus(status);

        if (challengeStatus == null) {
            log.error("PUT operation failed due to invalid challenge status parameter");
            return Mono.error(new IllegalArgumentException("Status null or not allowed"));
        }

        return saveValidSolution(userUuid, challengeUuid, languageUuid, challengeStatus, solutionAttempt)
                .map(savedDocument -> UserSolutionResponseDto.builder()
                        .userId(String.valueOf(savedDocument.getUserId()))
                        .languageId(String.valueOf(savedDocument.getLanguageId()))
                        .challengeId(String.valueOf(savedDocument.getChallengeId()))
                        .solutionText(savedDocument.getSolutionAttemptDocument().getSolutionText())
                        .build())
                .doOnSuccess(userSolutionDocument -> log.info("PUT request successfully processed and solution added to challenge {} for user {}.", userUuid, challengeUuid))
                .doOnError(error -> log.error("PUT operation failed with error message: {} for challenge {} and user {}.", error.getMessage(), challengeUuid, userUuid));
    }

    private Mono<UserSolutionDocument> saveValidSolution(UUID userUuid, UUID challengeUuid, UUID languageUuid, ChallengeStatus challengeStatus, SolutionAttemptDocument solutionAttempt) {
        return userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid)
                .flatMap(existingSolution -> {
                    if (existingSolution.getStatus() != null && existingSolution.getStatus().equals(ChallengeStatus.ENDED)) {
                        return Mono.error(new UnmodificableSolutionException("Existing solution for user " + userUuid +
                                " and challenge " + challengeUuid + " has status 'ENDED', and thus cannot be modified."));
                    }
                    existingSolution.setSolutionAttemptDocument(solutionAttempt);
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
                            .solutionAttemptDocument(solutionAttempt)
                            .build();
                    return userSolutionRepository.save(userSolutionDocument);
                }));
    }

}



