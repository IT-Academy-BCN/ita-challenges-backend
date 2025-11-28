package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.UserSubmissionResponseDto;
import reactor.core.publisher.Flux;

public interface IUserSubmissionService {

    Flux<UserSubmissionResponseDto> getAllSolutionsByUser(String userId);
}

