package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.PeerSubmissionItemDto;
import com.itachallenge.challenge.dto.submission.SubmissionActionResponseDto;
import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.challenge.dto.submission.SubmissionActionRequestDto;
import com.itachallenge.challenge.service.IChallengeJwtFacade;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.common.exception.BadRequestException;
import com.itachallenge.submission.document.SubmissionDocument;
import com.itachallenge.submission.enums.SubmissionAction;
import com.itachallenge.submission.enums.SubmissionStatus;
import com.itachallenge.submission.exception.UnmodifiableSubmissionException;
import com.itachallenge.submission.mapper.SubmissionMapper;
import com.itachallenge.submission.repository.SubmissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private static final List<SubmissionStatus> SUBMITTED_STATUSES = List.of(
            SubmissionStatus.SUBMITTED_COMPLETE,
            SubmissionStatus.SUBMITTED_INCOMPLETE
    );

    private final SubmissionRepository submissionRepository;
    private final IChallengeService challengeService;
    private final IChallengeJwtFacade challengeJwtFacade;

    public SubmissionServiceImpl(
            SubmissionRepository submissionRepository,
            IChallengeService challengeService,
            IChallengeJwtFacade challengeJwtFacade
    ) {
        this.challengeJwtFacade = challengeJwtFacade;
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
    public Mono<SubmissionActionResponseDto> processSubmissionAction(String userId, SubmissionActionRequestDto request, String authHeader) {
        String submittedByUsername = challengeJwtFacade.getUsernameFromAuthenticationHeader(authHeader);
        Mono<UUID> userUuidMono = validateAndParseUuid(userId);
        Mono<UUID> challengeUuidMono = Mono.justOrEmpty(request.getChallengeId())
                .switchIfEmpty(Mono.error(new BadRequestException("The 'challengeId' parameter cannot be null.")));
        Mono<UUID> languageUuidMono = Mono.justOrEmpty(request.getLanguageId())
                .switchIfEmpty(Mono.error(new BadRequestException("The 'languageId' parameter cannot be null.")));

        return Mono.zip(userUuidMono, challengeUuidMono, languageUuidMono)
                .flatMap(tuple -> {
                    Mono<SubmissionDocument> savedMono = validateSubmitText(request)
                            .then(Mono.defer(() -> saveOrUpdateSubmission(
                                    tuple.getT1(), tuple.getT2(), tuple.getT3(), request, submittedByUsername)));
                    return savedMono.flatMap(saved -> buildResponse(saved, tuple.getT2()));
                });
    }

    @Override
    public Flux<PeerSubmissionItemDto> getPeerSubmissions(UUID challengeId, UUID userId) {
        return submissionRepository.existsByUserIdAndChallengeIdAndStatusIn(userId, challengeId, SUBMITTED_STATUSES)
                .flatMapMany(hasSubmitted -> Boolean.TRUE.equals(hasSubmitted)
                        ? submissionRepository.findTop10ByChallengeIdAndUserIdNotAndStatusInOrderByCreatedAtDesc(
                                challengeId, userId, SUBMITTED_STATUSES)
                        .map(this::toPeerSubmissionItemDto)
                        : Flux.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User must have submitted the challenge before viewing peer submissions.")));

    }

    private Mono<Void> validateSubmitText(SubmissionActionRequestDto request) {
        if (request.getAction() == SubmissionAction.SUBMIT
                && (request.getSubmissionText() == null || request.getSubmissionText().isBlank())) {
            return Mono.error(new BadRequestException("The 'submissionText' parameter cannot be blank when action is SUBMIT."));
        }
        return Mono.empty();
    }

    private boolean isSubmitted(SubmissionStatus status) {
        return status == SubmissionStatus.SUBMITTED_COMPLETE || status == SubmissionStatus.SUBMITTED_INCOMPLETE;
    }

    private Mono<SubmissionDocument> saveOrUpdateSubmission(UUID userUuid, UUID challengeUuid, UUID languageUuid,
                                                            SubmissionActionRequestDto request, String submittedByUsername) {
        SubmissionStatus targetStatus = request.getAction().toStatus();
        return submissionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid)
                .flatMap(existing -> updateExistingAndSave(existing, targetStatus, request, submittedByUsername))
                .switchIfEmpty(Mono.defer(() -> createAndSave(userUuid, challengeUuid, languageUuid, targetStatus, request, submittedByUsername)));
    }

    private Mono<SubmissionDocument> updateExistingAndSave(SubmissionDocument existing, SubmissionStatus targetStatus,
                                                           SubmissionActionRequestDto request, String submittedByUsername) {
        if (isSubmitted(existing.getStatus())) {
            return Mono.error(new UnmodifiableSubmissionException("Submission cannot be modified once submitted."));
        }
        existing.setStatus(targetStatus);
        existing.setSubmissionText(request.getSubmissionText());
        if (existing.getCreatedAt() == null) {
            existing.setCreatedAt(LocalDateTime.now());
        }
        if (submittedByUsername != null) {
            existing.setSubmittedByUsername(submittedByUsername);
        }
        return submissionRepository.save(existing);
    }

    private Mono<SubmissionDocument> createAndSave(UUID userUuid, UUID challengeUuid, UUID languageUuid,
                                                   SubmissionStatus targetStatus, SubmissionActionRequestDto request, String submittedByUsername) {
        SubmissionDocument created = SubmissionDocument.builder()
                .submissionId(UUID.randomUUID())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(targetStatus)
                .submissionText(request.getSubmissionText())
                .createdAt(LocalDateTime.now())
                .submittedByUsername(submittedByUsername)
                .build();
        return submissionRepository.save(created);
    }

    private Mono<SubmissionActionResponseDto> buildResponse(SubmissionDocument saved, UUID challengeUuid) {
        if (saved.getStatus() == SubmissionStatus.SUBMITTED_COMPLETE) {
            return challengeService.addChallengeToSolved(challengeUuid.toString())
                    .map(solvedDto -> SubmissionActionResponseDto.builder()
                            .submissionText(saved.getSubmissionText())
                            .status(saved.getStatus().name())
                            .isSolved(true)
                            .timesSolved(solvedDto.getTimesSolved())
                            .build());
        }
        return Mono.just(SubmissionActionResponseDto.builder()
                .submissionText(saved.getSubmissionText())
                .status(saved.getStatus().name())
                .isSolved(false)
                .timesSolved(null)
                .build());
    }

    private Mono<UUID> validateAndParseUuid(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Mono.error(new BadRequestException("The 'userId' parameter cannot be null or empty."));
        }
        return Mono.fromCallable(() -> UUID.fromString(userId.trim()))
                .onErrorMap(IllegalArgumentException.class,
                        ex -> new BadRequestException("The 'userId' parameter must be a valid UUID."));
    }

    private PeerSubmissionItemDto toPeerSubmissionItemDto(SubmissionDocument doc) {
        return PeerSubmissionItemDto.builder()
                .submissionId(doc.getSubmissionId() != null ? doc.getSubmissionId().toString() : null)
                .challengeId(doc.getChallengeId() != null ? doc.getChallengeId().toString() : null)
                .userId(doc.getUserId() != null ? doc.getUserId().toString() : null)
                .languageId(doc.getLanguageId() != null ? doc.getLanguageId().toString() : null)
                .submittedAt(doc.getCreatedAt())
                .submissionText(doc.getSubmissionText())
                .status(doc.getStatus() != null ? doc.getStatus().name() : null)
                .author(doc.getSubmittedByUsername())
                .build();
    }
}