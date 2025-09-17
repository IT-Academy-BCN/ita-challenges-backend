package com.itachallenge.githubcore.service;

import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.exception.GithubUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class GithubApiServiceImpl implements GithubApiService {

    private static final Logger log = LoggerFactory.getLogger(GithubApiServiceImpl.class);
    private final WebClient webClient;

    public GithubApiServiceImpl(WebClient.Builder builder, String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Mono<GithubUserStatus> userExists(String username) {
        return webClient.get()
                .uri("/users/{username}", username)
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
}
