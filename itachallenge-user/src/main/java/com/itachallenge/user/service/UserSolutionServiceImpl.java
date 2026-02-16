package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionAttemptDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.document.enums.ChallengeStatus;
import com.itachallenge.user.document.enums.SolutionAction;
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
    private final IUserSolutionRepository userSolutionRepository;
    private final IChallengeService challengeService;

    public UserSolutionServiceImpl(IUserSolutionRepository userSolutionRepository, IChallengeService challengeService) {
        this.userSolutionRepository = userSolutionRepository;
        this.challengeService = challengeService;
    }

    //TODO : to be moved to the challenge micro when we do the entire solutions refactor
    @Override
    public Mono<SubmitSolutionResponseDto> addSolution(UserSolutionRequestDto userSolutionDto) {
        if (SolutionAction.SUBMIT.equals(userSolutionDto.getAction())) {
            if (userSolutionDto.getSolutionText() == null || userSolutionDto.getSolutionText().isBlank()) {
                return Mono.error(new BadRequestException("Solution text is required when finalizing (SUBMIT)."));
            }
        }
        UUID challengeUuid = UUID.fromString(userSolutionDto.getChallengeId());
        UUID languageUuid = UUID.fromString(userSolutionDto.getLanguageId());
        UUID userUuid = UUID.fromString(userSolutionDto.getUserId());

        ChallengeStatus challengeStatus = determineStatus(userSolutionDto.getAction());
        SolutionAttemptDocument solutionAttempt = SolutionAttemptDocument.builder()
                .uuid(UUID.randomUUID())
                .solutionText(userSolutionDto.getSolutionText())
                .build();

        return saveValidSolution(userUuid, challengeUuid, languageUuid, challengeStatus, solutionAttempt)
                            .flatMap(this::buildSubmitSolutionResponse)
                            .doOnSuccess(response -> log.info("PUT request successfully processed for challenge {} and user {}.", challengeUuid, userUuid))
                            .doOnError(error -> log.error("PUT operation failed: {} for challenge {} and user {}.", error.getMessage(), challengeUuid, userUuid));
    }

    //TODO : to be moved to the challenge micro when we do the entire solutions refactor
    private ChallengeStatus determineStatus(SolutionAction action) {
        return switch (action) {
            case SAVE -> ChallengeStatus.IN_PROGRESS;
            case GIVE_UP -> ChallengeStatus.SUBMITTED_INCOMPLETE;
            case SUBMIT -> ChallengeStatus.SUBMITTED_COMPLETE;
        };
    }

    //TODO : to be moved to the challenge micro when we do the entire solutions refactor
    private Mono<UserSolutionDocument> saveValidSolution(UUID userUuid, UUID challengeUuid, UUID languageUuid, ChallengeStatus challengeStatus, SolutionAttemptDocument solutionAttempt) {
        return userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid)
                .flatMap(existingSolution -> {
                    if (ChallengeStatus.SUBMITTED_COMPLETE.equals(existingSolution.getStatus()) ||
                            ChallengeStatus.SUBMITTED_INCOMPLETE.equals(existingSolution.getStatus()))
                    {
                        return Mono.error(new UnmodificableSolutionException("Existing solution is already submitted and cannot be modified."));
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

    //TODO : to be moved to the challenge micro when we do the entire solutions refactor
    private Mono<SubmitSolutionResponseDto> buildSubmitSolutionResponse(UserSolutionDocument savedDocument) {
        String solutionText = savedDocument.getSolutionAttemptDocument().getSolutionText();
        ChallengeStatus status = savedDocument.getStatus();

        if (ChallengeStatus.SUBMITTED_COMPLETE.equals(status)) {
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
}
