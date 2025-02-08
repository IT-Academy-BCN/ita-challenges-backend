package com.itachallenge.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final WebClient.Builder webClientBuilder;

    private static final String KEY_IS_VALID = "isValid";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String CLIENT_SECRET_KEY = "client_secret";
    private static final String CODE_KEY = "code";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String GITHUB_LOGIN_KEY = "login";

    @Value("${spring.security.oauth2.client.provider.github.user-info-uri}")
    private String githubUserInfoUri;

    @Value("${spring.security.oauth2.client.provider.github.token-uri}")
    private String githubTokenUri;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Autowired
    public AuthService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<String> exchangeCodeForToken(String code) {
        WebClient webClient = webClientBuilder.build();

        return webClient
                .post()
                .uri(githubTokenUri)
                .header("Accept", "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequestBody(clientId, clientSecret, code))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::processTokenResponse)
                .onErrorResume(this::handleTokenError);
    }

    private Map<String, String> createRequestBody(String clientId, String clientSecret, String code) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(CLIENT_ID_KEY, clientId);
        requestBody.put(CLIENT_SECRET_KEY, clientSecret);
        requestBody.put(CODE_KEY, code);
        return requestBody;
    }

    private Mono<String> processTokenResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
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

    private Mono<String> handleTokenError(Throwable ex) {
        log.error("Error exchanging code for token: {}", ex.getMessage());
        return Mono.error(ex);
    }

    @Override
    public Mono<Map<String, Object>> validateTokenWithGithub(String token) {
        WebClient webClient = webClientBuilder.build();

        return webClient
                .get()
                .uri(githubUserInfoUri)
                .header("Authorization", "token " + token)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(response);

                        if (jsonNode.has("login")) {
                            String githubUsername = jsonNode.get("login").asText();
                            log.info("GitHub username extracted: {}", githubUsername);

                            Map<String, Object> result = new HashMap<>();
                            result.put(KEY_IS_VALID, true);
                            result.put(KEY_USERNAME, githubUsername);
                            result.put("token", token);

                            return Mono.just(result);

                        } else {
                            log.error("GitHub response does not contain a username: {}", response);
                            Map<String, Object> errorResult = new HashMap<>();
                            errorResult.put(KEY_IS_VALID, false);
                            errorResult.put(KEY_USERNAME, null);
                            return Mono.just(errorResult);
                        }
                    } catch (JsonProcessingException e) {
                        log.error("Error processing GitHub response", e);
                        return Mono.error(e);
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("GitHub API error: {}", ex.getStatusCode());
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put(KEY_IS_VALID, false);
                    errorResult.put(KEY_USERNAME, null);
                    return Mono.just(errorResult);
                })
                .onErrorResume(ex -> {
                    log.error("Unexpected error: {}", ex.getMessage());
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put(KEY_IS_VALID, false);
                    errorResult.put(KEY_USERNAME, null);
                    return Mono.just(errorResult);
                });
    }



    // Old validation method
    @Value("${uri_validate_token}")
    private String validationUrl;
    public Mono<Boolean> validateWithSSO(String token) {

        WebClient webClient = webClientBuilder.build();
        return webClient
                .post()
                .uri(validationUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(token)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(response);
                        if (jsonNode.has("id")) {
                            log.info("Token is valid");
                            return Mono.just(true);
                        } else if (jsonNode.has("message")) {
                            String message = jsonNode.get("message").asText();
                            log.warn("Token is not valid: {}", message);
                            return Mono.just(false);
                        } else {
                            log.error("Unexpected JSON response format: {}", response);
                            return Mono.error(new IllegalStateException("Unexpected JSON response format"));
                        }
                    } catch (JsonProcessingException e) {
                        log.error("Error processing JSON response", e);
                        return Mono.error(e);
                    }
                })
                .onErrorResume(WebClientResponseException.class, responseException -> {
                    log.error("Error from SSO server [{}]: {}", validationUrl, responseException.getStatusCode());
                    return Mono.just(false);
                })
                .onErrorResume(ex -> {
                    log.error("Unexpected error [{}]: {}", validationUrl, ex.getMessage());
                    return Mono.just(false);
                });
    }

}
