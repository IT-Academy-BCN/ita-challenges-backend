package com.itachallenge.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.githubcore.service.GithubApiService;
import com.itachallenge.githubcore.service.GithubOAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService implements IAuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final GithubOAuthService githubOAuthService;
    private final GithubApiService githubApiService;
    private final ObjectMapper objectMapper;

    private static final String KEY_IS_VALID = "isValid";
    private static final String KEY_USERNAME = "username";
    private static final String ACCESS_TOKEN_KEY = "access_token";


    public AuthService(GithubOAuthService githubOAuthService, GithubApiService githubApiService, ObjectMapper objectMapper) {
        this.githubOAuthService = githubOAuthService;
        this.githubApiService = githubApiService;
        this.objectMapper = objectMapper;
    }

    private Mono<String> processTokenResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has(ACCESS_TOKEN_KEY)) {
                String accessToken = jsonNode.get(ACCESS_TOKEN_KEY).asText();
                log.info("Access token obtained successfully");
                return Mono.just(accessToken);
            } else {
                log.error("GitHub OAuth error: {}", response);
                return Mono.error(new IllegalStateException("Failed to obtain access token"));
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing GitHub OAuth response", e);
            return Mono.error(e);
        }
    }

    private Mono<Map<String, Object>> handleUnexpectedError(Throwable ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        return Mono.just(createErrorResult());
    }


    @Override
    public Mono<String> exchangeCodeForToken(String code) {
        return githubOAuthService.exchangeCodeForToken(code)
            .flatMap(this::processTokenResponse)
            .doOnSuccess(token -> log.info("Access token obtained successfully"))
            .doOnError(e -> log.error("Error exchanging code for token", e))
                .onErrorResume(ex -> {
                    log.error("Unexpected error exchanging code for token: {}", ex.getMessage());
                    return Mono.error(ex);
                });
    }

    @Override
    public Mono<Map<String, Object>> validateTokenWithGithub(String token) {
        return githubOAuthService.validateTokenWithGithub(token)
            .doOnSuccess(result -> log.info("Validation completed for token: {}", token.substring(0, 8) + "..."))
            .doOnError(e -> log.error("Validation failed", e))
            .onErrorResume(ex -> {
                log.error("Unexpected error validating token: {}", ex.getMessage());
                return Mono.just(createErrorResult());
            });
    }

    private Map<String, Object> createErrorResult() {
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put(KEY_IS_VALID, false);
        errorResult.put(KEY_USERNAME, null);
        return errorResult;
    }

}
