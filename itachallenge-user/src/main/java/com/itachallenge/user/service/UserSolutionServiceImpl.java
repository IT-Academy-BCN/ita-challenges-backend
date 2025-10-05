package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionAttemptDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.document.enums.ChallengeStatus;
import com.itachallenge.user.dto.SubmitSolutionResponseDto;
import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.dto.UserSolutionResponseDto;
import com.itachallenge.user.exception.BadRequestException;
import com.itachallenge.user.exception.UnmodificableSolutionException;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserSolutionServiceImpl implements IUserSolutionService {

    private static final Logger log = LoggerFactory.getLogger(UserSolutionServiceImpl.class);
    private final UserService userService;
    private final IUserSolutionRepository userSolutionRepository;
    private final IChallengeService challengeService;

    static final Integer POINTS_PER_SOLVED_CHALLENGE = 5;

    public UserSolutionServiceImpl(UserService userService, IUserSolutionRepository userSolutionRepository, IChallengeService challengeService) {
        this.userService = userService;
        this.userSolutionRepository = userSolutionRepository;
        this.challengeService = challengeService;
    }

    @Override
    public Mono<SubmitSolutionResponseDto> addSolution(UserSolutionRequestDto userSolutionDto) {
        UUID challengeUuid = UUID.fromString(userSolutionDto.getChallengeId());
        UUID languageUuid = UUID.fromString(userSolutionDto.getLanguageId());
        UUID userUuid = UUID.fromString(userSolutionDto.getUserId());

        ChallengeStatus challengeStatus = ChallengeStatus.challengeStatusFromString(userSolutionDto.getStatus());
        if (challengeStatus == null) {
            log.error("PUT operation failed due to invalid challenge status parameter");
            return Mono.error(new IllegalArgumentException("Status null or not allowed"));
        }

        SolutionAttemptDocument solutionAttempt = SolutionAttemptDocument.builder()
                .uuid(UUID.randomUUID())
                .solutionText(userSolutionDto.getSolutionText())
                .build();

        return saveValidSolution(userUuid, challengeUuid, languageUuid, challengeStatus, solutionAttempt)
                .flatMap(this::awardsPointsForSolvedChallenge)
                .flatMap(this::buildSubmitSolutionResponse)
                .doOnSuccess(response -> log.info("PUT request successfully processed for challenge {} and user {}.", challengeUuid, userUuid))
                .doOnError(error -> log.error("PUT operation failed: {} for challenge {} and user {}.", error.getMessage(), challengeUuid, userUuid));
    }

    private Mono<UserSolutionDocument> saveValidSolution(UUID userUuid, UUID challengeUuid, UUID languageUuid, ChallengeStatus challengeStatus, SolutionAttemptDocument solutionAttempt) {
        return userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid)
                .flatMap(existingSolution -> {
                    if (ChallengeStatus.ENDED.equals(existingSolution.getStatus())) {
                        return Mono.error(new UnmodificableSolutionException("Existing solution is already ENDED and cannot be modified."));
                    }
                    existingSolution.setSolutionAttemptDocument(solutionAttempt);
                    existingSolution.setStatus(challengeStatus);
                    return userSolutionRepository.save(existingSolution);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    UserSolutionDocument newSolution = UserSolutionDocument.builder()
                            .uuid(UUID.randomUUID())
                            .userId(userUuid)
                            .challengeId(challengeUuid)
                            .languageId(languageUuid)
                            .status(challengeStatus)
                            .solutionAttemptDocument(solutionAttempt)
                            .build();
                    return userSolutionRepository.save(newSolution);
                }));
    }

    private Mono<SubmitSolutionResponseDto> buildSubmitSolutionResponse(UserSolutionDocument savedDocument) {
        String solutionText = savedDocument.getSolutionAttemptDocument().getSolutionText();
        ChallengeStatus status = savedDocument.getStatus();

        if (ChallengeStatus.ENDED.equals(status)) {
            return challengeService.addChallengeToSolved(savedDocument.getChallengeId().toString())
                    .map(solvedDto -> SubmitSolutionResponseDto.builder()
                            .solutionText(solutionText)
                            .isSolved(solvedDto.isSolved())
                            .timesSolved(solvedDto.getTimesSolved())
                            .status(status.name())
                            .build());
        } else {
            // TODO: Enhance the response for non-ended statuses like IN_PROGRESS if additional info is needed
            return Mono.just(SubmitSolutionResponseDto.builder()
                    .solutionText(solutionText)
                    .isSolved(false)
                    .status(status.name())
                    .build());
        }
    }

    @Override
    public Flux<UserSolutionResponseDto> getAllSolutionsByUser(String userId) {
        return validateAndParseUuid(userId)
                .flatMapMany(uuid ->
                        userSolutionRepository.findAllByUserId(uuid)
                                .map(doc -> UserSolutionResponseDto.builder()
                                        .userId(doc.getUserId().toString())
                                        .challengeId(doc.getChallengeId().toString())
                                        .languageId(doc.getLanguageId().toString())
                                        .solutionText(doc.getSolutionAttemptDocument().getSolutionText())
                                        .status(doc.getStatus().name())
                                        .build())
                );
    }

    private Mono<UUID> validateAndParseUuid(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Mono.error(new BadRequestException("The 'userId' parameter cannot be null or empty."));
        }
        try {
            return Mono.just(UUID.fromString(userId.trim()));
        } catch (IllegalArgumentException ex) {
            return Mono.error(new BadRequestException("The 'userId' parameter must be a valid UUID: " + userId));
        }
    }

    public Mono<UserSolutionDocument> awardsPointsForSolvedChallenge(UserSolutionDocument document) {
        if (document.getStatus() != ChallengeStatus.ENDED) return Mono.just(document);
        return userService.addPointsToUser(document.getUserId().toString(), POINTS_PER_SOLVED_CHALLENGE)
                .doOnNext(success -> {
                    if (Boolean.TRUE.equals(success)) {
                        log.info("Awarded {} points to user {}", POINTS_PER_SOLVED_CHALLENGE, document.getUserId());
                    } else {
                        log.warn("Failed to award points to user {}", document.getUserId());
                    }
                })
                .doOnError(error -> log.error("Error awarding points to user {}: {}", document.getUserId(), error.getMessage()))
                .thenReturn(document);
    }
}

