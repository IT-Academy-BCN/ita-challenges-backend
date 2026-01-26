package com.itachallenge.githubcore.service;

import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.dto.GithubAuthResponseDto;
import com.itachallenge.githubcore.dto.GithubAuthRequestDto;
import reactor.core.publisher.Mono;

public interface GithubApiService {
    Mono<GithubUserStatus> userExists(String username);
    Mono<GithubAuthResponseDto> authenticate(GithubAuthRequestDto githubAuthRequestDto);
}
