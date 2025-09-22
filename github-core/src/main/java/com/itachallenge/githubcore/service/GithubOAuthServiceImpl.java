package com.itachallenge.githubcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.githubcore.config.GithubCoreProperties;
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
        private final GithubCoreProperties properties;

    public GithubOAuthServiceImpl(WebClient.Builder webClientBuilder,
                                  ObjectMapper objectMapper,
                                  GithubCoreProperties properties) {
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
        this.properties = properties;
        }

        @Override
        public Mono<String> exchangeCodeForToken(String code) {
            WebClient webClient = webClientBuilder.build();

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("client_id", properties.getClientId());
            requestBody.put("client_secret", properties.getClientSecret());
            requestBody.put("code", code);

            return webClient.post()
                    .uri(properties.getTokenUri())
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
                    .uri(properties.getUserInfoUri())
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
