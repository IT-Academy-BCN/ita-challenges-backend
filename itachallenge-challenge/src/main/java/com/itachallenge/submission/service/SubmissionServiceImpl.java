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
    public Flux<SubmissionResponseDto> getAllSubmissionsByUser(UUID userId) {
        return validateAndParseUuid(userId)
                .flatMapMany(uuid ->
                        submissionRepository.findAllByUserId(uuid)
                                .map(SubmissionResponseDtoMapper::toDto)
                );
    }

    private Mono<UUID> validateAndParseUuid(UUID userId) {
        if (userId == null){
            return Mono.error(new BadRequestException("The 'userId' parameter cannot be null or empty."));
        }
        try {
            return Mono.just(userId);
        } catch (IllegalArgumentException ex) {
            return Mono.error(new BadRequestException("The 'userId' parameter must be a valid UUID: " + userId));
        }
    }
}
