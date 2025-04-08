package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.FavoriteDto;
import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.exception.JwtException;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.challenge.service.JwtServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(value = "/itachallenge/api/v1/favorites")
public class FavoritesController {

    private static final Logger log = LoggerFactory.getLogger(FavoritesController.class);

    @NonNull
    private final JwtServiceImpl jwtService;

    @NonNull
    private final IChallengeService challengeService;

    @PostMapping("/addChallenge/{challengeId}/favorites")
    @Operation(
            operationId = "Add a challenge to User's favorites.",
            summary = "Add a challenge to favorites.",
            description = "The ID Challenge sent through the URI is added to the user's favorites. User Id is determined from the headers.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = FavoriteDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Missing or invalid authorization header."),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public Mono<ResponseEntity<FavoriteDto>> addChallengeToFavorite(
            @PathVariable String challengeId,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {
        return Mono.fromCallable(() -> jwtService.getUserUuIdFromAuthenticationHeader(authHeader))
                .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
                .flatMap(userId -> challengeService.addChallengeToFavorites(challengeId, userId))
                .doOnError(error -> log.error("Error adding challenge to favorites: {}", error.getMessage()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/removeChallenge/{challengeId}/favorites")
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
    public Mono<ResponseEntity<FavoriteDto>> removeChallengeFromFavorite(
            @PathVariable String challengeId,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {
        return Mono.fromCallable(() -> jwtService.getUserUuIdFromAuthenticationHeader(authHeader))
                .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
                .flatMap(userId -> challengeService.removeChallengeFromFavorites(challengeId, userId))
                .doOnError(error -> log.error("Error removing challenge from favorites: {}", error.getMessage()))
                .map(ResponseEntity::ok);
    }
}
