package com.itachallenge.user.service;

import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.dto.UserSolutionResponseDto;
import reactor.core.publisher.Mono;

public interface IUserSolutionService {
    Mono<UserSolutionResponseDto> addSolution(UserSolutionRequestDto userSolutionDto);
}