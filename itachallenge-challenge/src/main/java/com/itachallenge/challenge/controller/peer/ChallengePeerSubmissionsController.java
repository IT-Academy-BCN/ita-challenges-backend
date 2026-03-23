package com.itachallenge.challenge.controller.peer;

import com.itachallenge.challenge.dto.MessageDto;
import com.itachallenge.challenge.dto.submission.PeerSubmissionItemDto;
import com.itachallenge.challenge.service.IChallengeJwtFacade;
import com.itachallenge.common.exception.BadRequestException;
import com.itachallenge.submission.service.SubmissionService;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/itachallenge/api/v1/challenges")
public class ChallengePeerSubmissionsController {

    private final SubmissionService submissionService;
    private final IChallengeJwtFacade challengeJwtFacade;

    @GetMapping("/{challengeId}/peer-submissions")
    @Operation(
            operationId = "getPeerSubmissions",
            summary = "Get peer submissions for a challenge",
            description = "Returns up to 10 most recent submissions from other students for the given challenge. " +
                    "The requesting user must have already submitted the challenge (SUBMITTED_COMPLETE or SUBMITTED_INCOMPLETE). " +
                    "Otherwise returns 403 Forbidden. " +
                    "The `author` field can be null for legacy submissions.",
            parameters = {
                    @Parameter(name = "challengeId", in = ParameterIn.PATH, required = true, description = "Challenge UUID"),
                    @Parameter(name = "Authorization", in = ParameterIn.HEADER, required = true, description = "Bearer token")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of up to 10 peer submissions, ordered by date descending",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PeerSubmissionItemDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Missing/invalid authorization or invalid challengeId",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User has not submitted the challenge yet",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(schema = @Schema(implementation = MessageDto.class))
                    )
            }
    )
    public Mono<ResponseEntity<List<PeerSubmissionItemDto>>> getPeerSubmissions(
            @PathVariable UUID challengeId,
            @RequestHeader(name = "Authorization") String authHeader
    ) {
        return Mono.fromCallable(() -> challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .onErrorMap(JwtException.class, e -> new BadRequestException(e.getMessage()))
                .map(UUID::fromString)
                .onErrorMap(IllegalArgumentException.class, e -> new BadRequestException("Invalid UUID for userId."))
                .flatMap(userId -> submissionService.getPeerSubmissions(challengeId, userId)
                        .collectList()
                        .map(ResponseEntity::ok));
    }
}

