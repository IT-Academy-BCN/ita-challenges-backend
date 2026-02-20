package com.itachallenge.challenge.controller.submission;

import com.itachallenge.challenge.dto.submission.SubmissionActionRequestDto;
import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.gamification.service.PointsService;
import com.itachallenge.submission.enums.SubmissionStatus;
import com.itachallenge.submission.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import com.itachallenge.challenge.dto.submission.SubmissionActionResponseDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;

import java.util.UUID;


@RestController
@Validated
@RequestMapping("/itachallenge/api/v1/users/{userId}/submissions")
public class SubmissionController {

    private static final Logger log = LoggerFactory.getLogger(SubmissionController.class);

    private final SubmissionService submissionService;
    private final PointsService pointsService;
    private final int pointsOnSubmissionComplete;

    public SubmissionController(
            SubmissionService submissionService,
            PointsService pointsService,
            @Value("${gamification.points.submission-complete:10}") int pointsOnSubmissionComplete
    ) {
        this.submissionService = submissionService;
        this.pointsService = pointsService;
        this.pointsOnSubmissionComplete = pointsOnSubmissionComplete;
    }

    @GetMapping
    @Operation(
            summary = "Get submissions by userId",
            description = "Returns all submissions for the given user. Empty array if none.",
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
                            description = "OK – empty array if none",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SubmissionDto.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Malformed UUID")
            }
    )
    public Flux<SubmissionDto> getAllSubmissionsByUser(@PathVariable String userId) {
        return submissionService.getAllSubmissionsByUser(userId);
    }

    @PostMapping
    @Operation(
            summary = "Create or update a submission",
            description = "Creates or updates a user submission depending on the action (SAVE, SUBMIT, GIVE_UP).",
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
                            description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SubmissionActionResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid UUID or action"),
                    @ApiResponse(responseCode = "409", description = "Submission already completed"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            }
    )
    public Mono<ResponseEntity<SubmissionActionResponseDto>> createOrUpdateSubmission(
            @PathVariable String userId,
            @Valid @RequestBody SubmissionActionRequestDto request
    ) {
        return submissionService.processSubmissionAction(userId, request)
                .flatMap(response ->
                        recordPointsIfSubmissionCompleted(userId, request, response)
                                .thenReturn(response)
                )
                .map(ResponseEntity::ok);
    }

    private Mono<Void> recordPointsIfSubmissionCompleted(
            String userId,
            SubmissionActionRequestDto request,
            SubmissionActionResponseDto response
    ) {
        if (!SubmissionStatus.SUBMITTED_COMPLETE.name().equals(response.getStatus())) {
            return Mono.empty();
        }

        try {
            UUID userUuid = UUID.fromString(userId.trim());
            UUID challengeUuid = request.getChallengeId();
            if (challengeUuid == null) {
                return Mono.empty();
            }

            return pointsService.recordPoints(userUuid, challengeUuid, pointsOnSubmissionComplete)
                    .onErrorResume(ex -> {
                        log.warn("Gamification recordPoints failed for userId={} challengeId={}: {}",
                                userUuid, challengeUuid, ex.getMessage());
                        return Mono.empty();
                    });
        } catch (IllegalArgumentException ex) {
            log.warn("Skipping gamification due to invalid UUID in controller path: {}", userId);
            return Mono.empty();
        }
    }
}
