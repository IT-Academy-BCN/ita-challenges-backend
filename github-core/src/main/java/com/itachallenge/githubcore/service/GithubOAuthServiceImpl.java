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
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class GithubOAuthServiceImpl implements GithubOAuthService {

        private static final Logger log = LoggerFactory.getLogger(GithubApiServiceImpl.class);

        private final WebClient.Builder webClientBuilder;
        private final ObjectMapper objectMapper;
        private final String githubTokenUri;
        private final String githubUserInfoUri;
        private final String clientId;
        private final String clientSecret;

        public GithubOAuthServiceImpl(WebClient.Builder webClientBuilder,
                                    ObjectMapper objectMapper,
                                    @Value("${spring.security.oauth2.client.provider.github.token-uri}") String githubTokenUri,
                                    @Value("${spring.security.oauth2.client.provider.github.user-info-uri}") String githubUserInfoUri,
                                    @Value("${spring.security.oauth2.client.registration.github.client-id}") String clientId,
                                    @Value("${spring.security.oauth2.client.registration.github.client-secret}") String clientSecret) {
            this.webClientBuilder = webClientBuilder;
            this.objectMapper = objectMapper;
            this.githubTokenUri = githubTokenUri;
            this.githubUserInfoUri = githubUserInfoUri;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        @Override
        public Mono<String> exchangeCodeForToken(String code) {
            WebClient webClient = webClientBuilder.build();

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("client_id", clientId);
            requestBody.put("client_secret", clientSecret);
            requestBody.put("code", code);

            return webClient.post()
                    .uri(githubTokenUri)
                    .header("Accept", "application/json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(this::processTokenResponse);
        }

    private Mono<String> processTokenResponse(String response) {
        try {
            ObjectMapper objMapper = new ObjectMapper();
            JsonNode jsonNode = objMapper.readTree(response);

            if (jsonNode.has("access_token")) {
                String accessToken = jsonNode.get("access_token").asText();
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

        @Override
        public Mono<String> getUsernameFromToken(String token) {
            WebClient webClient = webClientBuilder.build();

            return webClient.get()
                    .uri(githubUserInfoUri)
                    .header("Authorization", "token " + token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(this::extractUsername);
        }

        private Mono<String> extractUsername(String response) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response);
                if (jsonNode.has("login")) {
                    return Mono.just(jsonNode.get("login").asText());
                }
                return Mono.error(new IllegalStateException("No login field in GitHub response"));
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
        }

    }
