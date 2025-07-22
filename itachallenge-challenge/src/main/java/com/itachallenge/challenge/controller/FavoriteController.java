package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.FavoriteDto;
import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.exception.JwtException;
import com.itachallenge.challenge.service.IFavoriteService;
import com.itachallenge.jwtcore.service.IJwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/itachallenge/api/v1/favorite/challenges/")
public class FavoriteController {

    private final IFavoriteService favoriteService;
    private final IJwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(FavoriteController.class);

    public FavoriteController(IFavoriteService favoriteService, IJwtService jwtService) {
        this.favoriteService = favoriteService;
        this.jwtService = jwtService;
    }

    @PostMapping("/{challengeId}")
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
    public Mono<ResponseEntity<FavoriteDto>> addFavorite(
            @PathVariable String challengeId,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {
        return Mono.fromCallable(() -> jwtService.getUserUuIdFromAuthenticationHeader(authHeader))
                .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
                .flatMap(userId -> favoriteService.addChallengeToFavorites(challengeId, userId))
                .doOnError(e -> log.error("Failed to add favorite for challengeId {}: {}", challengeId, e.getMessage()))
                .map(ResponseEntity::ok);
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
        return Mono.fromCallable(() -> jwtService.getUserUuIdFromAuthenticationHeader(authHeader))
                .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
                .flatMap(userId -> favoriteService.removeChallengeFromFavorites(challengeId, userId))
                .map(ResponseEntity::ok);
    }
}
