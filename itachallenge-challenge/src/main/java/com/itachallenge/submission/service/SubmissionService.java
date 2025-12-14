package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.SubmissionResponseDto;
import reactor.core.publisher.Flux;

public interface SubmissionService {

    Flux<SubmissionResponseDto> getAllSubmissionsByUser(String userId);
}
