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

@Service
public class AuthService implements IAuthService {



    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final WebClient.Builder webClientBuilder;
    @Autowired
    public AuthService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
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
