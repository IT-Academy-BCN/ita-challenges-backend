package com.itachallenge.githubcore.service;

import com.itachallenge.githubcore.config.GithubProperties;
import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.dto.GithubAuthResponseDto;
import com.itachallenge.githubcore.dto.GithubAuthRequestDto;
import com.itachallenge.githubcore.exception.GithubUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

public class GithubApiServiceImpl implements GithubApiService {

    private static final Logger log = LoggerFactory.getLogger(GithubApiServiceImpl.class);
    private final WebClient apiWebClient;
    private final WebClient authWebClient;
    private final GithubProperties githubProperties;

    public GithubApiServiceImpl(GithubProperties githubProperties, WebClient.Builder builder) {
        this.githubProperties = githubProperties;

        this.apiWebClient = builder.baseUrl(githubProperties.getBaseApiUrl()).build();
        this.authWebClient = builder.baseUrl(githubProperties.getBaseAuthUrl()).build();
    }

    @Override
    public Mono<GithubUserStatus> userExists(String username) {
        return apiWebClient.get()
                .uri(githubProperties.getUserInfoUri() + "/{username}", username)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return Mono.just(GithubUserStatus.FOUND);
                    } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        log.info("GitHub user not found: {}", username);
                        return Mono.just(GithubUserStatus.NOT_FOUND);
                    } else if (response.statusCode().is5xxServerError()) {
                        log.error("GitHub API error for user: {}", username);
                        return Mono.error(new GithubUnavailableException("GitHub API error"));
                    } else {
                        log.error("Unexpected response from GitHub: {}", response.statusCode());
                        return Mono.error(new GithubUnavailableException("Unexpected response from GitHub"));
                    }
                });
    }

    @Override
    public Mono<GithubAuthResponseDto> authenticate(GithubAuthRequestDto githubAuthRequestDto) {
        return authWebClient.post()
                .uri(githubProperties.getTokenUri())
                .header("Accept", "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("client_id", githubProperties.getClientId(),
                        "client_secret", githubProperties.getClientSecret(),
                        "code", githubAuthRequestDto.code()))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    String token = (String) response.get("access_token");
                    if (token == null) return Mono.error(new GithubUnavailableException("Invalid GitHub code"));
                    return fetchUserProfile(token);
                });
    }

    private Mono<GithubAuthResponseDto> fetchUserProfile(String token) {
        return apiWebClient.get()
                .uri(githubProperties.getUserProfileUri())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Map.class)
                .map(profile -> new GithubAuthResponseDto((String) profile.get("login")))
                .onErrorResume(e -> Mono.error(new GithubUnavailableException("Error fetching GitHub profile")));
    }
}
