package com.itachallenge.challenge.service;

import com.itachallenge.challenge.exception.CustomBadRequestException;
import com.itachallenge.challenge.exception.CustomInternalServerErrorException;
import com.itachallenge.challenge.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final WebClient.Builder webClientBuilder;

    private final String userServiceUrl;

    public UserService(
            WebClient.Builder webClientBuilder,
            @Value("${user.service.url}") String userServiceUrl) {
        this.webClientBuilder = webClientBuilder;
        this.userServiceUrl = userServiceUrl;
    }

    @Override
    public Mono<Boolean> addChallengeToFavorites(String userId, String challengeId) {
        String url = userServiceUrl + "/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId;
        log.debug("Call to endpoint: {}", url);

        return webClientBuilder.build()
                .post()
                .uri(url)
                .retrieve()
                .onStatus(
                        HttpStatus.NOT_FOUND::equals, response -> {
                            log.info("User not found {}", userId);
                            return Mono.error(new UserNotFoundException("User not found"));
                        })
                .onStatus(
                        HttpStatus.BAD_REQUEST::equals, response -> {
                            String errorMessage = response.headers().header("X-Favorite-Message").stream()
                                    .findFirst().orElse("Unknown error");
                            log.warn("UserService returned 400: {}", errorMessage);
                            return Mono.error(new CustomBadRequestException(errorMessage));
                        })
                .onStatus(
                        HttpStatus.INTERNAL_SERVER_ERROR::equals, response -> {
                            String errorMessage = response.headers().header("X-Favorite-Message").stream()
                                    .findFirst().orElse("Unknown error");
                            log.warn("UserService returned 500: {}", errorMessage);
                            return Mono.error(new CustomInternalServerErrorException(errorMessage));
                        })
                .bodyToMono(Boolean.class);
    }

}
