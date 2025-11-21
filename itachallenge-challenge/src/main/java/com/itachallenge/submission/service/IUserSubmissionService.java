package com.itachallenge.submission.service;

import com.itachallenge.submission.dto.UserSubmissionResponseDto;
import reactor.core.publisher.Flux;

public interface IUserSubmissionService {

    Flux<UserSubmissionResponseDto> getAllSubmissionsByUser(String userId);
}

