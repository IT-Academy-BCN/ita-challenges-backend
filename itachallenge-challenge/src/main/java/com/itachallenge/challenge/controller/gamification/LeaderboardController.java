package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.MessageDto;
import com.itachallenge.challenge.dto.gamification.LeaderboardResponseDto;
import com.itachallenge.challenge.dto.gamification.WeeklyLeaguesResponseDto;
import com.itachallenge.gamification.service.LeaderboardService;
import com.itachallenge.gamification.service.WeeklyLeaguesResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/itachallenge/api/v1/leaderboard")
public class LeaderboardController {

    private static final Logger log = LoggerFactory.getLogger(LeaderboardController.class);
    private final LeaderboardService leaderboardService;

    @GetMapping
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
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - Database unavailable or unexpected error",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))

                    )
            }
    )
    public Mono<ResponseEntity<LeaderboardResponseDto>> getLeaderboard() {
        log.info("Receiving request to fetch global leaderboard");
        return leaderboardService.getLeaderboard()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/weekly")
    @Operation(
            summary = "Get weekly leagues ranking.",
            description = "Returns users split into Gold, Silver and Bronze leagues for the current week.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WeeklyLeaguesResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - Database unavailable or unexpected error",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    public Mono<ResponseEntity<WeeklyLeaguesResponseDto>> getWeeklyLeagues() {
        log.info("Receiving request to fetch weekly leagues leaderboard");
        return leaderboardService.getWeeklyLeagues()
                .map(this::mapToWeeklyLeaguesResponse)
                .map(ResponseEntity::ok);
    }

    private WeeklyLeaguesResponseDto mapToWeeklyLeaguesResponse(WeeklyLeaguesResult result) {
        return WeeklyLeaguesResponseDto.builder()
                .gold(result.getGold())
                .silver(result.getSilver())
                .bronze(result.getBronze())
                .build();
    }
}
