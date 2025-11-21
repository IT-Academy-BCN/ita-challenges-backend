package com.itachallenge.submission.service;

import com.itachallenge.submission.dto.UserSubmissionResponseDto;
import com.itachallenge.submission.repository.IUserSubmissionRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

import com.itachallenge.user.dto.UserSolutionResponseDto;
import com.itachallenge.user.exception.BadRequestException;
import com.itachallenge.user.service.IChallengeService;
import org.slf4j.Logger;


@Service
public class UserSubmissionServiceImpl implements IUserSubmissionService {
    private static final Logger log = LoggerFactory.getLogger(UserSubmissionServiceImpl.class);
    private final IUserSubmissionRepository userSubmissionRepository;
    private final IChallengeService challengeService;

    public UserSubmissionServiceImpl(IUserSubmissionRepository userSolutionRepository, IChallengeService challengeService) {
        this.userSubmissionRepository = userSubmissionRepository;
        this.challengeService = challengeService;
    }
    @Override
    public Flux<UserSubmissionResponseDto> getAllSolutionsByUser(String userId) {
        return validateAndParseUuid(userId)
                .flatMapMany(uuid ->
                        userSubmissionRepository.findAllByUserId(uuid)
                                .map(doc -> UserSubmissionResponseDto.builder()
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
}