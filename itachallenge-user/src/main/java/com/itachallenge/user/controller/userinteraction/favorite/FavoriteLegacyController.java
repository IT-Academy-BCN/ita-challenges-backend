package com.itachallenge.user.controller.userinteraction.favorite;

import com.itachallenge.userinteraction.service.favorite.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/itachallenge/api/v1/userinteraction/favorites")
public class FavoriteLegacyController {

    private static  final String DEPRECATION = "deprecation";
    @Value("${app.api.favorites.successor-version.post-path}")
    private String postPath;

    @Value("${app.api.favorites.successor-version.get-path}")
    private String getPath;

    @Value("${app.api.favorites.successor-version.delete-path}")
    private String deletePath;

    private static final Logger log = LoggerFactory.getLogger(FavoriteLegacyController.class);

    private final FavoriteService favoriteService;

    public FavoriteLegacyController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }
    /**
     * @deprecated
     */
    @Operation(
            summary = "Add Challenge to User Favorite Challenges",
            description = "Adds challenge to user favorites",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "User ID",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "challengeId",
                            description = "Challenge ID",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Challenge is already in favorites",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "201",
                            description = "Challenge added to favorites",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request. The provided IDs have a bad format",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found. No user is found with the provided user id.",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred.",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/users/{userId}/favorites/{challengeId}")
    @Deprecated(since="3.2.2", forRemoval = true)
    public Mono<ResponseEntity<Boolean>> addToFavoritesLegacy(@PathVariable String userId, @PathVariable String challengeId) {
        return favoriteService.addChallengeToFavorites(userId, challengeId)
                .map(added -> {
                    if (Boolean.TRUE.equals(added)) {
                        log.info("Challenge '{}' added to user '{}' favorites", challengeId, userId);
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .header(DEPRECATION,"true")
                                .header("Link", postPath)
                                .body(true);
                    } else {
                        log.info("User's '{}' favorites already contain Challenge '{}'", userId, challengeId);
                        return ResponseEntity.ok()
                                .header(DEPRECATION,"true")
                                .header("Link", postPath)
                                .body(false);
                    }
                });
    }
    /**
     * @deprecated
     */
    @Operation(
            summary = "Gets challenges marked as favorites by a user",
            description = "Returns all favorites that the specified user has marked.",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "UUID of the user",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Set of favorite challengeIds by user"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "The provided IDs are not valid."),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            }
    )
    @GetMapping("/{userId}")
    @Deprecated(since="3.2.2", forRemoval = true)
    public Mono<ResponseEntity<Set<UUID>>> getUserFavoritesLegacy(@PathVariable String userId) {
        return favoriteService.getUserFavorites(userId)
                .map(favorites -> {
                    log.info("Retrieved {} favorite challenges for user {}", favorites.size(), userId);
                    return ResponseEntity.ok()
                            .header(DEPRECATION,"true")
                            .header("Link", getPath)
                            .body(favorites);
                });
    }
    /**
     * @deprecated
     */
    @Operation(
            summary = "Delete Challenge from User Favorite Challenges",
            description = "Deletes challenge from user favorites",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "User ID",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "challengeId",
                            description = "Challenge ID",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Challenge deleted from favorites or was not in favorites",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request. The provided IDs have a bad format",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found. No user is found with the provided user id.",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred.",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @DeleteMapping("/users/{userId}/favorites/{challengeId}")
    @Deprecated(since="3.2.2", forRemoval = true)
    public Mono<ResponseEntity<Boolean>> deleteFromFavoritesLegacy(@PathVariable String userId, @PathVariable String challengeId) {
        return favoriteService.deleteChallengeFromFavorites(userId, challengeId)
                .map(deleted -> {
                    if (Boolean.TRUE.equals(deleted)) {
                        log.info("Challenge '{}' deleted from user '{}' favorites", challengeId, userId);
                        return ResponseEntity.ok()
                                .header(DEPRECATION,"true")
                                .header("Link", deletePath)
                                .body(true);
                    } else {
                        log.info("No change, User's '{}' favorites doesn't contain Challenge '{}'", userId, challengeId);
                        return ResponseEntity.ok()
                                .header(DEPRECATION,"true")
                                .header("Link", deletePath)
                                .body(false);
                    }
                });
    }
}
