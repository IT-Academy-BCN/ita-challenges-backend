package com.itachallenge.auth.service;

import com.itachallenge.auth.dto.User;
import com.itachallenge.auth.exception.CustomBadRequestException;
import com.itachallenge.auth.exception.CustomInternalServerErrorException;
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
    public Mono<User> fetchUserData(String githubUsername) {
        String url = userServiceUrl + "/itachallenge/api/v1/user/users/" + githubUsername;
        log.debug("Fetching user data from: {}", url);

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .onStatus(
                        HttpStatus.NOT_FOUND::equals, response -> {
                            log.info("User not found {}", githubUsername);
                            return Mono.empty();
                        })
                .onStatus(
                        HttpStatus.BAD_REQUEST::equals, response -> {
                            String errorMessage = response.headers().header("X-Error-Message").stream()
                                    .findFirst().orElse("Unknown error");
                            log.warn("UserService returned 400: {}", errorMessage);
                            return Mono.error(new CustomBadRequestException(errorMessage));
                        })

                .onStatus(
                        HttpStatus.INTERNAL_SERVER_ERROR::equals, response -> {
                            String errorMessage = response.headers().header("X-Error-Message").stream()
                                    .findFirst().orElse("Unknown error");
                            log.warn("UserService returned 500: {}", errorMessage);
                            return Mono.error(new CustomInternalServerErrorException(errorMessage));
                        })
                .bodyToMono(User.class);
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
