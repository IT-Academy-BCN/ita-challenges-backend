package com.itachallenge.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.service.GithubApiService;
import com.itachallenge.githubcore.service.GithubOAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService implements IAuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final String KEY_IS_VALID = "isValid";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";


    private final GithubOAuthService githubOAuthService;
    private final GithubApiService githubApiService;

    public AuthService(GithubOAuthService githubOAuthService, GithubApiService githubApiService){
        this.githubOAuthService = githubOAuthService;
        this.githubApiService = githubApiService;
    }

    public Mono<String> exchangeCodeForToken(String code) {
        return githubOAuthService.exchangeCodeForToken(code)
                .doOnSuccess(token -> log.info("Access token obtained successfully"))
                .doOnError(e -> log.error("Error exchanging code for token"));
    }



    private Mono<String> handleTokenError(Throwable ex) {
        log.error("Error exchanging code for token: {}", ex.getMessage());
        return Mono.error(ex);
    }

    @Override
    public Mono<Map<String, Object>> validateTokenWithGithub(String token) {
        return githubOAuthService.getUsernameFromToken(token)
                .map(username -> {
                    log.info("GitHub username extracted: {}", username);
                    return createSuccessResult(username, token);
                })
                .onErrorResume(e -> {
                    log.error("Error validating GitHub token", e);
                    return Mono.just(createErrorResult());
                });
    }

    public Mono<GithubUserStatus> checkUserExists(String username) {
        return githubApiService.userExists(username);
    }

    private Mono<Map<String, Object>> handleGithubApiError(WebClientResponseException ex) {
        log.error("GitHub API error: {}", ex.getStatusCode());
        return Mono.just(createErrorResult());
    }

    private Mono<Map<String, Object>> handleUnexpectedError(Throwable ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        return Mono.just(createErrorResult());
    }

    private Map<String, Object> createSuccessResult(String username, String token) {
        Map<String, Object> result = new HashMap<>();
        result.put(KEY_IS_VALID, true);
        result.put(KEY_USERNAME, username);
        result.put(KEY_TOKEN, token);
        return result;
    }

    private Map<String, Object> createErrorResult() {
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put(KEY_IS_VALID, false);
        errorResult.put(KEY_USERNAME, null);
        return errorResult;
    }

}
