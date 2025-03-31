package com.itachallenge.challenge.service;

import com.itachallenge.challenge.enums.SavedItemType;
import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.challenge.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final WebClient.Builder webClientBuilder;

    private final String userServiceUrl;
    private final String X_FAVORITE_MESSAGE = "X-Favorite-Message";
    private final String X_BOOKMARK_MESSAGE = "X-Bookmark-Message";

    public UserService(
            WebClient.Builder webClientBuilder,
            @Value("${user.service.url}") String userServiceUrl) {
        this.webClientBuilder = webClientBuilder;
        this.userServiceUrl = userServiceUrl;
    }

    @Override
    public Mono<Boolean> addChallengeToFavorites(String userId, String challengeId) {
        return addChallengeToUserTag(userId, challengeId, SavedItemType.FAVORITES, X_FAVORITE_MESSAGE, HttpMethod.POST);
    }

    @Override
    public Mono<Boolean> addChallengeToBookmarks(String userId, String challengeId) {
        return addChallengeToUserTag(userId, challengeId, SavedItemType.BOOKMARKS, X_BOOKMARK_MESSAGE, HttpMethod.POST);
    }

    @Override
    public Mono<Boolean> removeChallengeFromFavorites(String userId, String challengeId) {
        return callFavoriteEndpoint(userId, challengeId, HttpMethod.DELETE);
    }

    private Mono<Boolean> addChallengeToUserTag(String userId, String challengeId, SavedItemType type, String errorHeader) {
        String url = buildUrl(userId, challengeId, type.toString().toLowerCase());
        log.debug("Call to endpoint: {}", method, url);

        return webClientBuilder.build()
                .method(method)
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    log.info("User not found {}", userId);
                    return Mono.error(new UserNotFoundException("User not found"));
                })
                .onStatus(HttpStatus.BAD_REQUEST::equals, response -> {
                    String errorMessage = response.headers().header(errorHeader).stream()
                            .findFirst().orElse("Unknown error");
                    log.warn("UserService returned 400: {}", errorMessage);
                    return Mono.error(new BadRequestException(errorMessage));
                })
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response -> {
                    String errorMessage = response.headers().header(errorHeader).stream()
                            .findFirst().orElse("Unknown error");
                    log.warn("UserService returned 500: {}", errorMessage);
                    return Mono.error(new InternalServerErrorException(errorMessage));
                })
                .bodyToMono(Boolean.class);
    }

    private String buildUrl(String userId, String challengeId, String type){
        return UriComponentsBuilder.fromHttpUrl(userServiceUrl)
                .path("/itachallenge/api/v1/user/users/{userId}/{type}/{challengeId}")
                .buildAndExpand(userId, challengeId)
                .toUriString();
    }
}
