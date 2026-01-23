package com.itachallenge.submission.controller;

import com.itachallenge.challenge.dto.submission.SubmissionRequestDto;
import com.itachallenge.challenge.dto.submission.SubmissionResponseDto;
import com.itachallenge.submission.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/itachallenge/api/v1")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @Operation(summary = "Create or update a submission")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submission saved or submitted"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Submission already completed"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    @PostMapping("/users/{userId}/submissions")
    public Mono<ResponseEntity<SubmissionResponseDto>> createOrUpdateSubmission(
            @PathVariable String userId,
            @RequestBody SubmissionRequestDto request
    ) {
        return submissionService.createOrUpdateSubmission(userId, request)
                .map(ResponseEntity::ok);
    }
}
