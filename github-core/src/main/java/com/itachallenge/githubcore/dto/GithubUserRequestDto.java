package com.itachallenge.githubcore.dto;

public record GithubUserRequestDto(
        String code,
        String clientId,
        String clientSecret
) {
}
