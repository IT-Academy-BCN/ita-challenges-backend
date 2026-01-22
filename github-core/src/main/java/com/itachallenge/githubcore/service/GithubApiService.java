package com.itachallenge.githubcore.service;

import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.dto.GithubUserResponseDto;
import com.itachallenge.githubcore.dto.GithubUserRequestDto;
import reactor.core.publisher.Mono;

public interface GithubApiService {
    Mono<GithubUserStatus> userExists(String username);
    Mono<GithubUserResponseDto> authenticate(GithubUserRequestDto githubUserRequestDto);
}
