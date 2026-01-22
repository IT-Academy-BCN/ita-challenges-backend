package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.challenge.dto.submission.SubmissionRequestDto;
import com.itachallenge.challenge.dto.submission.SubmissionResponseDto;
import com.itachallenge.submission.document.SubmissionDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SubmissionService {

    Flux<SubmissionDto> getAllSubmissionsByUser(String userId);

    Mono<SubmissionResponseDto> createOrUpdateSubmission(String userId, SubmissionRequestDto request);

}
