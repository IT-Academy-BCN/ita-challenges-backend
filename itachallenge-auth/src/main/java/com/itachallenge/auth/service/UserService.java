package com.itachallenge.auth.service;

import com.itachallenge.auth.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final WebClient.Builder webClientBuilder;

    private final String userServiceUrl;

    public UserService(WebClient.Builder webClientBuilder,
                       @Value("${user.service.url}") String userServiceUrl) {
        this.webClientBuilder = webClientBuilder;
        this.userServiceUrl = userServiceUrl;
    }

    @Override
    public Mono<ResponseEntity<User>> forwardUserDetails(String githubUsername) {
        String url = userServiceUrl + "/itachallenge/api/v1/user/users/" + githubUsername;
        log.debug("Request URL: {}", url);

        return webClientBuilder.build()
                .get()
                .uri(url)
                .exchangeToMono(clientResponse ->
                        clientResponse.toEntity(User.class)
                )
                .map(responseEntity -> ResponseEntity
                                .status(responseEntity.getStatusCode())
                                .headers(responseEntity.getHeaders())
                                .body(responseEntity.getBody())
                );
    }

    @Override
    public Mono<String> callUserTest() {
        return webClientBuilder.build()
                .get()
                .uri(userServiceUrl + "/itachallenge/api/v1/user/test")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(ex -> {
                    log.error("Error calling User microservice: {}", ex.getMessage());
                    return Mono.just("Error calling User microservice");
                });
    }

}
