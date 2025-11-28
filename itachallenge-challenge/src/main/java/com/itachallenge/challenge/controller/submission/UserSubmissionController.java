package com.itachallenge.challenge.controller.submission;

import com.itachallenge.challenge.dto.submission.UserSubmissionResponseDto;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.submission.service.IUserSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/itachallenge/api/v1/challenge")
public class UserSubmissionController {

    private static final Logger log = LoggerFactory.getLogger(UserSubmissionController.class);
    private final IUserSubmissionService userSubmissionService;

    @GetMapping("/challenges/{userId}/submissions")
    @Operation(
            operationId = "getSubmissionsByUserId",
            summary = "Get submissions by userId",
            description = "Retrieve submissions for a given user from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval - may return empty array if no submissions found",
                            content = @Content(schema = @Schema(implementation = UserSubmissionResponseDto.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Malformed UUID or invalid parameters"),
            })
    public Flux<UserSubmissionResponseDto> getAllSubmissionsByUser(
            @PathVariable String userId) {

        try {
            UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new BadUUIDException("Invalid UUID format");
        }

        return userSubmissionService.getAllSubmissionsByUser(userId);

    }
}

