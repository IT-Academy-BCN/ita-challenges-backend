package com.itachallenge.submission.service;

import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.submission.dto.UserSubmissionResponseDto;
import com.itachallenge.submission.repository.IUserSubmissionRepository;
import com.itachallenge.challenge.service.IChallengeService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

import org.slf4j.Logger;


@Service
public class UserSubmissionServiceImpl implements IUserSubmissionService {
    private static final Logger log = LoggerFactory.getLogger(UserSubmissionServiceImpl.class);
    private final IUserSubmissionRepository userSubmissionRepository;
    //private final IChallengeService challengeService; NO NECESARIO PARA LA TASKA 883 REFACTOR GET

    public UserSubmissionServiceImpl(IUserSubmissionRepository userSubmissionRepository, IChallengeService challengeService) {
        this.userSubmissionRepository = userSubmissionRepository;
        //this.challengeService = challengeService; NO NECESARIO PARA LA TASKA 883 REFACTOR GET
    }
    @Override
    public Flux<UserSubmissionResponseDto> getAllSubmissionsByUser(String userId) {
        return validateAndParseUuid(userId)
                .flatMapMany(uuid ->
                        userSubmissionRepository.findAllByUserId(uuid)
                                .map(doc -> UserSubmissionResponseDto.builder()
                                        .userId(doc.getUserId().toString())
                                        .challengeId(doc.getChallengeId().toString())
                                        .languageId(doc.getLanguageId().toString())
                                        .solutionText(doc.getSubmissionAttemptDocument().getSubmissionText())
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
}