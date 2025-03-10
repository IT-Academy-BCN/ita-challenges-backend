package com.itachallenge.challenge.config;

import com.itachallenge.challenge.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserClient {

    private final WebClient userWebClient;

    @Autowired
    public UserClient(WebClient userWebClient) {
        this.userWebClient = userWebClient;
    }

    public Mono<UserDto> getUserByUsername(String username) {
        return userWebClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new RuntimeException("User not found"))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException("Server error in User Service"))
                )
                .bodyToMono(UserDto.class);
    }
}
