package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.MessageDto;
import com.itachallenge.challenge.dto.gamification.ActivityTypeResponseDto;
import com.itachallenge.gamification.service.ActivityTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/itachallenge/api/v1/activity-types")
public class ActivityTypeController {

    private final ActivityTypeService activityTypeService;

    @GetMapping
    @Operation(
            summary = "Get available activity types.",
            description = "Returns the list of available activity types that generate points in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK - returns an object with an empty activityTypes list if none.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ActivityTypeResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    public Mono<ActivityTypeResponseDto> getAvailableActivityTypes() {
        log.info("Requesting available activity types");
        return activityTypeService.getAvailableActivityTypes();
    }
}
