package com.itachallenge.submission.service;

import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.submission.dto.UserSubmissionResponseDto;
import com.itachallenge.submission.repository.IUserSubmissionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
public class UserSubmissionServiceImpl implements IUserSubmissionService {
    private final IUserSubmissionRepository userSubmissionRepository;

    public UserSubmissionServiceImpl(IUserSubmissionRepository userSubmissionRepository) {
        this.userSubmissionRepository = userSubmissionRepository;
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
                                        .submissionText(doc.getSubmissionAttemptDocument().getSubmissionText())
                                        .action(doc.getAction().name())
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
