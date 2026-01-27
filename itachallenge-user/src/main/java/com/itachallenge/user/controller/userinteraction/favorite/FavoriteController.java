package com.itachallenge.user.controller.userinteraction.favorite;

import com.itachallenge.userinteraction.service.favorite.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

// TODO [TECH-DEBT][Taiga-#938]:
// Refactor endpoint structure to treat favorites as a user subresource
// and remove duplicated path segments. See Taiga task for details.

@RestController
@RequestMapping("/itachallenge/api/v1/userinteraction/favorites")
public class FavoriteController {

    private static final Logger log = LoggerFactory.getLogger(FavoriteController.class);

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

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
    public Mono<ResponseEntity<Boolean>> addToFavorites(@PathVariable String userId, @PathVariable String challengeId) {
        return favoriteService.addChallengeToFavorites(userId, challengeId)
                .map(added -> {
                    if (Boolean.TRUE.equals(added)) {
                        log.info("Challenge '{}' added to user '{}' favorites", challengeId, userId);
                        return ResponseEntity.status(HttpStatus.CREATED).body(true);
                    } else {
                        log.info("User's '{}' favorites already contain Challenge '{}'", userId, challengeId);
                        return ResponseEntity.ok().body(false);
                    }
                });
    }
    //NEW PATH
    @Operation(
            summary = "Gets challenges marked as favorites by a user",
            description = "Returns all favorites for the specified user with identity validation.",
            parameters = {
                    @Parameter(name = "userId", description = "UUID of the user", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Set of favorite challengeIds by user"),
                    @ApiResponse(responseCode = "400", description = "Invalid token or ID mismatch"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            }
    )
    @GetMapping("/users/{userId}/favorites")
    public Mono<ResponseEntity<Set<UUID>>> getUserFavorites(@PathVariable String userId) {
        return favoriteService.getUserFavorites(userId)
                .map(ResponseEntity::ok);
    }
    //LEGACY PATH - to be refactored in the future
    /**
     * @deprecated since 3.1.4. Use {@link #getUserFavorites(String)} instead.
     */
    @Operation(
            summary = "Gets challenges marked as favorites by a user (LEGACY)",
            description = "Legacy endpoint with deprecation warning.",
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
    @Deprecated(since = "3.1.4", forRemoval = true)
    @SuppressWarnings("java:S1133")
    @GetMapping("/{userId}")
    public Mono<ResponseEntity<Set<UUID>>> getUserFavoritesLegacy(@PathVariable String userId) {
        return favoriteService.getUserFavorites(userId)
                .map(favorites -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Deprecation", "true");
                    headers.add("Link", "</itachallenge/api/v1/userinteraction/favorites/users/" + userId + "/favorites>; rel=\"successor-version\"");                    log.info("Retrieved {} favorite challenges for user {}", favorites.size(), userId);
                    return ResponseEntity.ok().headers(headers).body(favorites);
                });
    }

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
    public Mono<ResponseEntity<Boolean>> deleteFromFavorites(@PathVariable String userId, @PathVariable String challengeId) {
        return favoriteService.deleteChallengeFromFavorites(userId, challengeId)
                .map(deleted -> {
                    if (Boolean.TRUE.equals(deleted)) {
                        log.info("Challenge '{}' deleted from user '{}' favorites", challengeId, userId);
                        return ResponseEntity.ok().body(true);
                    } else {
                        log.info("No change, User's '{}' favorites doesn't contain Challenge '{}'", userId, challengeId);
                        return ResponseEntity.ok().body(false);
                    }
                });
    }
}
