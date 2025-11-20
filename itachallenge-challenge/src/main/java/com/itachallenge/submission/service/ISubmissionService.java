package com.itachallenge.submission.service;

import com.itachallenge.submission.dto.SubmissionResponseDto;
import reactor.core.publisher.Flux;

public interface ISubmissionService {

    Flux<SubmissionResponseDto> getAllSolutionsByUser(String userId);
}

