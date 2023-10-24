package com.itachallenge.user.service;

import com.itachallenge.user.dtos.SolutionUserDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SolutionService {
    Mono<SolutionUserDto> createUserSolution(UUID idUser, UUID idChallenge, UUID idLanguage, String solutionText);
}

