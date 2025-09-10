package com.itachallenge.githubcore.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class GithubApiService implements IGithubApiService{

    private static final Logger log = LoggerFactory.getLogger(GithubApiService.class);
    private final WebClient webClient;

    public GithubApiService(WebClient.Builder builder, String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<Boolean> userExists(String username) {
        return webClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, r -> {
                    log.info("GitHub user not found: {}", username);
                    return Mono.error(new RuntimeException("User not found"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, r -> {
                    log.info("GitHub API error: {}", username);
                    return Mono.error(new RuntimeException("GitHub API error"));
                }
                )
                .toBodilessEntity()
                .map(response -> true)
                .defaultIfEmpty(false);
    }

}
