package com.itachallenge.submission.service;

import com.itachallenge.user.dto.SubmitSolutionResponseDto;
import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.dto.UserSolutionResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SubmissionService {

    Mono<SubmitSolutionResponseDto> addSolution(UserSolutionRequestDto userSolutionDto);
    Flux<UserSolutionResponseDto> getAllSolutionsByUser(String userId);
}

