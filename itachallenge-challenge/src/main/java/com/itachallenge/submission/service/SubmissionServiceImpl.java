package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.common.exception.BadRequestException;
import com.itachallenge.submission.document.SubmissionDocument;
import com.itachallenge.submission.mapper.SubmissionMapper;
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
    public Flux<SubmissionDto> getAllSubmissionsByUser(String userId) {
        return validateAndParseUuid(userId)
                .flatMapMany(uuid ->
                        submissionRepository.findAllByUserId(uuid)
                                .map(SubmissionMapper::toDto)
                );
    }

    private Mono<UUID> validateAndParseUuid(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Mono.error(new BadRequestException("The 'userId' parameter cannot be null or empty."));
        }
        return Mono.fromCallable(() -> UUID.fromString(userId.trim()))
                .onErrorMap(IllegalArgumentException.class,
                        ex -> new BadRequestException("The 'userId' parameter must be a valid UUID."));
    }
}
