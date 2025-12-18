package com.itachallenge.challenge.service;

import com.itachallenge.challenge.enums.UserChallengeActionType;
import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.challenge.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final WebClient.Builder webClientBuilder;

    private final String userServiceUrl;
    private final String X_FAVORITE_MESSAGE = "X-Favorite-Message";
    private final String X_BOOKMARK_MESSAGE = "X-Bookmark-Message";
    private final String favoritesPath;
    private final String bookmarksPath;

    public UserServiceImpl(
            WebClient.Builder webClientBuilder,
            @Value("${user.service.url}") String userServiceUrl,
            @Value("${user.endpoints.favorites}") String favoritesPath,
            @Value("${user.endpoints.bookmarks}") String bookmarksPath) {
        this.webClientBuilder = webClientBuilder;
        this.userServiceUrl = userServiceUrl;
        this.favoritesPath = favoritesPath;
        this.bookmarksPath = bookmarksPath;
    }

    @Override
    public Mono<Boolean> addChallengeToFavorites(String userId, String challengeId) {
        return callEndpoint(userId, challengeId, UserChallengeActionType.FAVORITES, X_FAVORITE_MESSAGE, HttpMethod.POST);
    }

    @Override
    public Mono<Boolean> addChallengeToBookmarks(String userId, String challengeId) {
        return callEndpoint(userId, challengeId, UserChallengeActionType.BOOKMARKS, X_BOOKMARK_MESSAGE, HttpMethod.POST);
    }

    @Override
    public Mono<Boolean> removeChallengeFromFavorites(String userId, String challengeId) {
        return callEndpoint(userId, challengeId, UserChallengeActionType.FAVORITES, X_FAVORITE_MESSAGE, HttpMethod.DELETE);
    }

    @Override
    public Mono<Boolean> removeChallengeFromBookmarks(String userId, String challengeId) {
        return callEndpoint(userId, challengeId, UserChallengeActionType.BOOKMARKS, X_BOOKMARK_MESSAGE, HttpMethod.DELETE);
    }

    private Mono<Boolean> callEndpoint(String userId, String challengeId, UserChallengeActionType type, String errorHeader, HttpMethod method) {
        String url = buildUrl(userId, challengeId, type);
        log.debug("Calling {} endpoint with method={} and URL={}", type.name().toLowerCase(), method, url);

        return webClientBuilder.build()
                .method(method)
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    log.info("User not found with id: {}", userId);
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

    private String buildUrl(String userId, String challengeId, UserChallengeActionType type){

        String template = switch (type) {
            case FAVORITES -> favoritesPath;
            case BOOKMARKS -> bookmarksPath;
        };

        return UriComponentsBuilder.fromHttpUrl(userServiceUrl)
                .path(template)
                .buildAndExpand(userId, challengeId)
                .toUriString();
    }
}
