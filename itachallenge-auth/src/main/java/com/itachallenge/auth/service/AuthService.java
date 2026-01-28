package com.itachallenge.auth.service;

import com.itachallenge.githubcore.dto.GithubAuthRequestDto;
import com.itachallenge.githubcore.service.GithubApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService implements IAuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final GithubApiService githubApiService;

    private static final String KEY_IS_VALID = "isValid";
    private static final String KEY_USERNAME = "username";

    public AuthService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Mono<Map<String, Object>> authenticateWithGithub(String code) {
        return githubApiService.authenticate(new GithubAuthRequestDto(code))
                .map(authResponse -> {
                    if (authResponse.username() == null || authResponse.username().isBlank()) {
                        log.warn("GitHub returned a response with no username");
                        return createErrorResult();
                    }
                    return createSuccessResult(authResponse.username());
                })
                .onErrorResume(ex -> {
                    log.error("Authentication error with the GitHub library: {}", ex.getMessage());
                    return Mono.just(createErrorResult());
                });
    }

    private Map<String, Object> createSuccessResult(String username) {
        Map<String, Object> result = new HashMap<>();
        result.put(KEY_IS_VALID, true);
        result.put(KEY_USERNAME, username);
        return result;
    }

    private Map<String, Object> createErrorResult() {
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put(KEY_IS_VALID, false);
        errorResult.put(KEY_USERNAME, null);
        return errorResult;
    }
}
