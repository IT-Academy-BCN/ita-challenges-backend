package com.itachallenge.challenge.controller.submission;

import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.submission.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("/itachallenge/api/v1/users/{userId}/submissions")
public class SubmissionController {

    private static final Logger log = LoggerFactory.getLogger(SubmissionController.class);
    private final SubmissionService submissionService;

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
}
