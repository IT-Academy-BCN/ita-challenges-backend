package com.itachallenge.challenge.controller.submission;

import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.submission.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/itachallenge/api/v1/submissions")
public class SubmissionController {

    private static final Logger log = LoggerFactory.getLogger(SubmissionController.class);
    private final SubmissionService submissionService;

    @GetMapping
    @Operation(
            operationId = "getSubmissionsByUserId",
            summary = "Get submissions by userId",
            description = "Retrieve submissions for a given user from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval - may return empty array if no submissions found",
                            content = @Content(schema = @Schema(implementation = SubmissionDto.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Malformed UUID or invalid parameters"),
            })
    public Flux<SubmissionDto> getAllSubmissionsByUser(@RequestParam String userId) {
        return submissionService.getAllSubmissionsByUser(userId);
    }
}
