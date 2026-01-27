package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.challenge.dto.submission.SubmissionRequestDto;
import com.itachallenge.challenge.dto.submission.SubmissionResponseDto;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.common.exception.BadRequestException;
import com.itachallenge.submission.document.SubmissionDocument;
import com.itachallenge.submission.enums.SubmissionAction;
import com.itachallenge.submission.enums.SubmissionStatus;
import com.itachallenge.submission.exception.UnmodifiableSubmissionException;
import com.itachallenge.submission.mapper.SubmissionMapper;
import com.itachallenge.submission.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final IChallengeService challengeService;


    public SubmissionServiceImpl(SubmissionRepository submissionRepository, IChallengeService challengeService) {
        this.submissionRepository = submissionRepository;
        this.challengeService = challengeService;
    }

    @Override
    public Flux<SubmissionDto> getAllSubmissionsByUser(String userId) {
        return validateAndParseUuid(userId)
                .flatMapMany(uuid ->
                        submissionRepository.findAllByUserId(uuid)
                                .map(SubmissionMapper::toDto)
                );
    }

    @Override
    public Mono<SubmissionResponseDto> createOrUpdateSubmission(String userId, SubmissionRequestDto request) {

        Mono<UUID> userUuidMono = validateAndParseUuid(userId);

        Mono<UUID> challengeUuidMono = Mono.justOrEmpty(request.getChallengeId())
                .switchIfEmpty(Mono.error(new BadRequestException("The 'challengeId' parameter cannot be null.")));

        Mono<UUID> languageUuidMono = Mono.justOrEmpty(request.getLanguageId())
                .switchIfEmpty(Mono.error(new BadRequestException("The 'languageId' parameter cannot be null.")));


        return Mono.zip(userUuidMono, challengeUuidMono, languageUuidMono)
                .flatMap(tuple -> {
                    UUID userUuid = tuple.getT1();
                    UUID challengeUuid = tuple.getT2();
                    UUID languageUuid = tuple.getT3();

                    SubmissionAction action = SubmissionAction.fromString(request.getAction());
                    SubmissionStatus targetStatus = action.toStatus();

                    return submissionRepository
                            .findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid)
                            .flatMap(existing -> {
                                if (existing.getStatus() == SubmissionStatus.SUBMITTED_COMPLETE
                                        || existing.getStatus() == SubmissionStatus.SUBMITTED_INCOMPLETE) {
                                    return Mono.error(new UnmodifiableSubmissionException(
                                            "Submission cannot be modified once submitted."));
                                }

                                existing.setStatus(targetStatus);
                                existing.setSubmissionText(request.getSubmissionText());
                                return submissionRepository.save(existing);
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                SubmissionDocument created = SubmissionDocument.builder()
                                        .submissionId(UUID.randomUUID())
                                        .userId(userUuid)
                                        .challengeId(challengeUuid)
                                        .languageId(languageUuid)
                                        .status(targetStatus)
                                        .submissionText(request.getSubmissionText())
                                        .build();

                                return submissionRepository.save(created);
                            }))
                            .flatMap(saved -> {
                                if (saved.getStatus() == SubmissionStatus.SUBMITTED_COMPLETE) {
                                    return challengeService.addChallengeToSolved(challengeUuid.toString())
                                            .map(solvedDto -> SubmissionResponseDto.builder()
                                                    .submissionText(saved.getSubmissionText())
                                                    .status(saved.getStatus().name())
                                                    .isSolved(true)
                                                    .timesSolved(solvedDto.getTimesSolved())
                                                    .build());
                                }

                                return Mono.just(SubmissionResponseDto.builder()
                                        .submissionText(saved.getSubmissionText())
                                        .status(saved.getStatus().name())
                                        .isSolved(false)
                                        .timesSolved(null)
                                        .build());
                            });
                });
    }


    private Mono<UUID> validateAndParseUuid(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Mono.error(new BadRequestException("The 'userId' parameter cannot be null or empty."));
        }
        return Mono.fromCallable(() -> UUID.fromString(userId.trim()))
                .onErrorMap(IllegalArgumentException.class,
                        ex -> new BadRequestException("The 'userId' parameter must be a valid UUID."));
    }

    private Mono<UUID> validateAndParseUuid(String value, String fieldName) {
        return Mono.fromCallable(() -> UUID.fromString(value.trim()))
                .onErrorMap(IllegalArgumentException.class,
                        ex -> new BadRequestException("The '" + fieldName + "' parameter must be a valid UUID."));
    }

}