package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.SubmissionResponseDto;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SubmissionService {

    Flux<SubmissionResponseDto> getAllSubmissionsByUser(UUID userId);
}
