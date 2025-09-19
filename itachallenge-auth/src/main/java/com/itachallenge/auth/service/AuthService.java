package com.itachallenge.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.githubcore.service.GithubOAuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService implements IAuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final GithubOAuthServiceImpl githubOAuthService;
    private final ObjectMapper objectMapper;

    private static final String KEY_IS_VALID = "isValid";
    private static final String KEY_USERNAME = "username";
    private static final String ACCESS_TOKEN_KEY = "access_token";

    public AuthService(GithubOAuthServiceImpl githubOAuthService, ObjectMapper objectMapper) {
        this.githubOAuthService = githubOAuthService;
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


    public Mono<Map<String, Object>> authenticateWithGithub(String code) {
        return githubOAuthService.exchangeCodeForToken(code)
                .flatMap(this::processTokenResponse)
                .flatMap(token ->
                        githubOAuthService.validateTokenWithGithub(token)
                                .onErrorResume(this::handleUnexpectedError)
                )
                .onErrorResume(this::handleUnexpectedError);
    }

    @Override
    public Mono<String> exchangeCodeForToken(String code) {
        return githubOAuthService.exchangeCodeForToken(code);
    }

    @Override
    public Mono<Map<String, Object>> validateTokenWithGithub(String token) {
        return githubOAuthService.validateTokenWithGithub(token)
                .onErrorResume(this::handleUnexpectedError);
    }


    private Map<String, Object> createErrorResult() {
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put(KEY_IS_VALID, false);
        errorResult.put(KEY_USERNAME, null);
        return errorResult;
    }

}
