package com.itachallenge.submission.service;

import com.itachallenge.challenge.mapper.submission.SubmissionResponseDtoMapper;
import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.dto.submission.SubmissionResponseDto;

import com.itachallenge.submission.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }
    @Override
    public Flux<SubmissionResponseDto> getAllSubmissionsByUser(String userId) {
        return validateAndParseUuid(userId)
                .flatMapMany(uuid ->
                        submissionRepository.findAllByUserId(uuid)
                                .map(SubmissionResponseDtoMapper::toDto)
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
