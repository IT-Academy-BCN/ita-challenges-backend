package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.challenge.service.IChallengeJwtFacade;
import com.itachallenge.gamification.service.PointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

    @Slf4j
    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/itachallenge/api/v1/me/points/history")
    public class UserScoreHistoryController {

        private final PointsService pointsService;
        private final IChallengeJwtFacade jwtFacade;

        @GetMapping
        @Operation(
                summary = "Get authenticated user's points history.",
                description = "Retrieves the point accumulation history and total points for the user identified by the JWT token.",
                responses = {
                        @ApiResponse(
                                responseCode = "200",
                                description = "OK",
                                content = @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PointsHistoryResponseDto.class)
                                )
                        ),
                        @ApiResponse(
                                responseCode = "401",
                                description = "Unauthorized - Invalid or missing token."
                        ),
                        @ApiResponse(
                                responseCode = "500",
                                description = "Internal Server Error."
                        )
                }
        )
        public Mono<PointsHistoryResponseDto> getUserPointsHistory(
                @RequestHeader("Authorization") String authHeader) {

            log.info("Requesting points history for authenticated user");

            String userIdStr = jwtFacade.getUserUuIdFromAuthenticationHeader(authHeader);
            UUID userId = UUID.fromString(userIdStr);

            return pointsService.getUserPointsHistory(userId);
        }
}
