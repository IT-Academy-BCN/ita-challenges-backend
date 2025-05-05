package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.SolvedDto;
import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.exception.JwtException;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.challenge.service.IJwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/challenge")
@RequiredArgsConstructor
public class ChallengeSolvedController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeSolvedController.class);

    private IChallengeService challengeService;
    private IJwtService jwtService;

    @PostMapping("/challenges/{challengeId}/solved")
    @Operation(
            operationId = "Add a challenge to User's solved challenges.",
            summary = "Add a challenge to solved challenges.",
            description = "The ID Challenge sent through the URI is added to the user's solved challenges. User Id is determined from the headers.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = SolvedDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Missing or invalid authorization header."),
                    @ApiResponse(responseCode = "404", description = "The Challenge with given Id was not found."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public Mono<ResponseEntity<SolvedDto>> addChallengeToSolved(
            @PathVariable String challengeId,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {
        return Mono.fromCallable(() -> jwtService.getUserUuIdFromAuthenticationHeader(authHeader))
                .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
                .flatMap(userId -> challengeService.addChallengeToSolved(challengeId, userId))
                .doOnError(error -> log.error("Error adding challenge to solved: {}", error.getMessage()))
                .map(ResponseEntity::ok);
    }
}
