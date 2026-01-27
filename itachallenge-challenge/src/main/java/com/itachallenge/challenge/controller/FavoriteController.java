package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.AddFavoriteRequest;
import com.itachallenge.challenge.dto.FavoriteDto;
import com.itachallenge.common.exception.BadRequestException;
import com.itachallenge.challenge.exception.JwtException;
import com.itachallenge.challenge.service.IFavoriteService;
import com.itachallenge.challenge.service.IChallengeJwtFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpHeaders;



@RestController
@RequestMapping("/itachallenge/api/v1/challenges/")
public class FavoriteController {

    private final IFavoriteService favoriteService;
    private final IChallengeJwtFacade challengeJwtFacade;
    private static final Logger log = LoggerFactory.getLogger(FavoriteController.class);

    public FavoriteController(IFavoriteService favoriteService, IChallengeJwtFacade challengeJwtFacade) {
        this.favoriteService = favoriteService;
        this.challengeJwtFacade = challengeJwtFacade;
    }
// ✅ NEW REST CONTRACT
@PostMapping("/users/{userId}/favorites")
@Operation(
        operationId = "Add a challenge to User's favorites (new REST contract).",
        summary = "Add a challenge to favorites.",
        description = "Adds a challenge to the user's favorites via User subresource.",
        responses = {
                @ApiResponse(responseCode = "201", description = "Favorite created successfully.",
                        content = {@Content(schema = @Schema(implementation = FavoriteDto.class), mediaType = "application/json")}),
                @ApiResponse(responseCode = "400", description = "Missing or invalid authorization header / ID mismatch."),
                @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found."),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
        }
)
public Mono<ResponseEntity<FavoriteDto>> addFavorite(
        @PathVariable String userId,
        @RequestHeader(name = "Authorization", required = false) String authHeader,
        @RequestBody AddFavoriteRequest request) {

    return Mono.fromCallable(() -> challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
            .onErrorMap(JwtException.class, e -> new BadRequestException("Invalid token"))
            .flatMap(userIdFromToken -> {
                if (!userIdFromToken.equals(userId)) {
                    return Mono.error(new BadRequestException("You cannot add favorites for another user."));
                }
                return favoriteService.addChallengeToFavorites(request.getChallengeId(), userId);
            })
            .doOnError(e -> log.error("Security violation or error for user {}: {}", userId, e.getMessage()))
            .map(dto -> ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(dto));
}

    // LEGACY UPDATED 200 TO 201 FOR CONSISTENCY
    /**
     * @deprecated since v2.0.4.
     * Use {@code POST /users/{userId}/favorites} instead.
     * This endpoint will be removed once all consumers migrate.
     */
    @Deprecated(since = "2.0.4")
    @SuppressWarnings("java:S1133")
    @PostMapping("/{challengeId}")
    @Operation(
            operationId = "Add a challenge to User's favorites.",
            summary = "Add a challenge to favorites.",
            description = "The ID Challenge sent through the URI is added to the user's favorites. User Id is determined from the headers.",
            responses = {
                    @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = FavoriteDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Missing or invalid authorization header."),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public Mono<ResponseEntity<FavoriteDto>> addFavoriteLegacy(
            @PathVariable String challengeId,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {
        return Mono.fromCallable(() -> challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
                .flatMap(userId -> favoriteService.addChallengeToFavorites(challengeId, userId))
                .doOnError(e -> log.error("Failed to add favorite for challengeId {}: {}", challengeId, e.getMessage()))
                .map(favoriteDto -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Deprecation", "true");
                    headers.add(
                            "Link",
                            "</users/{userId}/favorites>; rel=\"successor-version\""
                    );

                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(favoriteDto);
                });
    }

    @DeleteMapping("/{challengeId}")
    @Operation(
            operationId = "Remove a challenge from the User's favorites.",
            summary = "Remove a challenge from favorites.",
            description = "The ID Challenge sent through the URI is removed from the user's favorites. User Id is determined from the headers.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = FavoriteDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Missing or invalid authorization header."),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public Mono<ResponseEntity<FavoriteDto>> removeFavorite(
            @PathVariable String challengeId,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {
        return Mono.fromCallable(() -> challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
                .flatMap(userId -> favoriteService.removeChallengeFromFavorites(challengeId, userId))
                .map(ResponseEntity::ok);
    }
}

