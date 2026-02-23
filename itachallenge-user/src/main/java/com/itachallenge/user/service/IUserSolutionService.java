package com.itachallenge.user.service;

import com.itachallenge.user.dto.SubmitSolutionResponseDto;
import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.dto.UserSolutionResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserSolutionService {
    Mono<SubmitSolutionResponseDto> addSolution(UserSolutionRequestDto userSolutionDto);
}