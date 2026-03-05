package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.gamification.service.UserScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/itachallenge/api/v1/users/{userId}/scores")
public class UserScoreController {

    private final UserScoreService userScoreService;

    @GetMapping("/history")
    @Operation(
            summary = "Get user points history.",
            description = "Returns the total points and the history of points earned by a user.",
            parameters = {
                    @Parameter(
                            name = "userId",
                            in = ParameterIn.PATH,
                            required = true,
                            description = "User UUID"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK - empty array if none.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PointsHistoryResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid UUID format."
                    )
            }
    )
    public Mono<PointsHistoryResponseDto> getUserPointsHistory(@PathVariable String userId) {
        log.info("Requesting points history for user: {}", userId);
        UUID userUuid = UUID.fromString(userId);

        return userScoreService.getUserPointsHistory(userUuid);
    }
}
