package com.itachallenge.githubcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class GithubOAuthServiceImpl implements GithubOAuthService {

    private static final Logger log = LoggerFactory.getLogger(GithubOAuthServiceImpl.class);
    private final WebClient webClient;

    private static final String KEY_IS_VALID = "isValid";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String CLIENT_SECRET_KEY = "client_secret";
    private static final String CODE_KEY = "code";
    private static final String GITHUB_LOGIN_KEY = "login";

    private final String githubUserInfoUri;

    private final String githubTokenUri;

    private final String clientId;

    private final String clientSecret;
    private final ObjectMapper objectMapper;

    public GithubOAuthServiceImpl(WebClient.Builder webClientBuilder,
                       @Value("${spring.security.oauth2.client.provider.github.token-uri}") String githubTokenUri,
                       @Value("${spring.security.oauth2.client.provider.github.user-info-uri}") String githubUserInfoUri,
                       @Value("${spring.security.oauth2.client.registration.github.client-id}") String clientId,
                       @Value("${spring.security.oauth2.client.registration.github.client-secret}") String clientSecret,
                       ObjectMapper objectMapper

    ) {
        this.webClient = webClientBuilder.build();
        this.githubTokenUri = githubTokenUri;
        this.githubUserInfoUri = githubUserInfoUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.objectMapper = objectMapper;
    }

    public Mono<String> exchangeCodeForToken(String code) {

        return webClient
                .post()
                .uri(githubTokenUri)
                .header("Accept", "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequestBody(clientId, clientSecret, code))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(this::handleTokenError);
    }

    private Map<String, String> createRequestBody(String clientId, String clientSecret, String code) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(CLIENT_ID_KEY, clientId);
        requestBody.put(CLIENT_SECRET_KEY, clientSecret);
        requestBody.put(CODE_KEY, code);
        return requestBody;
    }

    private Mono<String> handleTokenError(Throwable ex) {
        log.error("Error exchanging code for token: {}", ex.getMessage());
        return Mono.error(ex);
    }

    @Override
    public Mono<Map<String, Object>> validateTokenWithGithub(String token) {

        return webClient
                .get()
                .uri(githubUserInfoUri)
                .header("Authorization", "token " + token)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> processGithubResponse(response, token))

                .onErrorResume(WebClientResponseException.class, this::handleGithubApiError)
                .onErrorResume(this::handleUnexpectedError);
    }

    private Mono<Map<String, Object>> processGithubResponse(String response, String token) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has(GITHUB_LOGIN_KEY)) {
                String githubUsername = jsonNode.get(GITHUB_LOGIN_KEY).asText();
                log.info("GitHub username extracted: {}", githubUsername);
                return Mono.just(createSuccessResult(githubUsername, token));
            } else {
                log.error("GitHub response does not contain a username: {}", response);
                return Mono.just(createErrorResult());
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing GitHub response", e);
            return Mono.error(e);
        }
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
