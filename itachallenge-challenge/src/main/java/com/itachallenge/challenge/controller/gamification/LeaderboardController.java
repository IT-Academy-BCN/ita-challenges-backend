package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import com.itachallenge.gamification.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/itachallenge/api/v1/users")
public class LeaderboardController {

    private static final Logger log =  LoggerFactory.getLogger(LeaderboardController.class);
    private final LeaderboardService leaderboardService;

    @GetMapping("/leaderboard")
    @Operation(
            summary = "Get global student ranking.",
            description = "Returns a list of students sorted by their total points in descending order.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LeaderboardResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error.")
            }
    )
    public Mono<LeaderboardResponseDto> getLeaderboard() {
        log.info("Receiving request to fetch leaderboard");
        return leaderboardService.getLeaderboard();
    }
}
