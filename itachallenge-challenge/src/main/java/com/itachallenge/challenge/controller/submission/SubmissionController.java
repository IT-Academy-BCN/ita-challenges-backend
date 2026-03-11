package com.itachallenge.challenge.controller.submission;

import com.itachallenge.challenge.dto.submission.SubmissionActionRequestDto;
import com.itachallenge.challenge.dto.submission.SubmissionActionResponseDto;
import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.submission.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;




@RestController
@Validated
@RequestMapping("/itachallenge/api/v1/users/{userId}/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
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
            description = "Creates or updates a user submission depending on the action (SAVE, SUBMIT, GIVE_UP). When the result is SUBMITTED_COMPLETE, the user's gamification score is updated (UserScoreService).",
            parameters = {
                    @Parameter(
                            name = "userId",
                            in = ParameterIn.PATH,
                            required = true,
                            description = "User UUID"
                    ),
                    @Parameter(
                            name = "Authorization",
                            in = ParameterIn.HEADER,
                            required = false,
                            description = "Bearer token; if present, the submitter's username is stored for peer-solutions author display"
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
            @Valid @RequestBody SubmissionActionRequestDto request,
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        return submissionService.processSubmissionAction(userId, request, authHeader)
                .map(ResponseEntity::ok);
    }

}
