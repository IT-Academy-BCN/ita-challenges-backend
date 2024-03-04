package com.itachallenge.user.controller;

import com.itachallenge.user.annotations.GenericUUIDValid;
import com.itachallenge.user.dtos.*;
import com.itachallenge.user.service.IServiceChallengeStatistics;
import com.itachallenge.user.service.IUserSolutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final IServiceChallengeStatistics serviceChallengeStatistics;
    private final IUserSolutionService userSolutionService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(
            IServiceChallengeStatistics serviceChallengeStatistics,
            IUserSolutionService userSolutionService) {
        this.serviceChallengeStatistics = serviceChallengeStatistics;
        this.userSolutionService = userSolutionService;
    }

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA User!!!";
    }

    @ApiResponse(responseCode = "404", description = "Not Found")
    @Operation(summary = "Get Basic Info of Challenge")
    @GetMapping(value = "/statistics/{challengeId}")
    public Mono<ResponseEntity<?>> getChallengeStatistics(
            @PathVariable("challengeId") UUID challengeId,
            @RequestHeader HttpHeaders headers) {

        // Check if challengeId is null
        if (challengeId == null) {
            logger.error("ChallengeId is null");
            return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }

        // Set the content type of the response
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Call the service to get challenge statistics
        return serviceChallengeStatistics.getChallengeStatistics(challengeId)
                .flatMap(challengeStatisticsDto -> {
                    // Check if the response entity is not null
                    if (challengeStatisticsDto != null) {
                        // If not null, return it as is
                        return Mono.just(new ResponseEntity<>(challengeStatisticsDto, HttpStatus.OK));
                    } else {
                        // If null, log an error and return a 404 response entity
                        logger.error("Response entity is null for challengeId: {}", challengeId);
                        return Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                    }
                })
                // If the mono is empty, use Mono.defer to create a new ResponseEntity with 404 status
                .switchIfEmpty(Mono.defer(() -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND))));
    }


    @GetMapping(path = "/solution/user/{idUser}/challenge/{idChallenge}/language/{idLanguage}")
    @Operation(
            summary = "obtains all the solutions to a challenge with the given language and user.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = SolutionUserDto.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "No user with the required id.", content = {@Content(schema = @Schema())})
            }
    )
    public Mono<SolutionUserDto<UserScoreDto>> getSolutionsByUserIdChallengeIdLanguageId(
            @PathVariable("idUser") @GenericUUIDValid(message = "Invalid UUID for user") String idUser,
            @PathVariable("idChallenge") @GenericUUIDValid(message = "Invalid UUID for challenge") String idChallenge,
            @PathVariable("idLanguage") @GenericUUIDValid(message = "Invalid UUID for language") String idLanguage) {
        return userSolutionService.getChallengeById(idUser, idChallenge, idLanguage);
    }

    @PutMapping(path = "/solution")
    @Operation(
            summary = "perform a solution, adding challenge,language,user and the corresponding solution text.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = SolutionUserDto.class),
                            mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Something went wrong",
                            content = {@Content(schema = @Schema())})
            }
    )
    public Mono<ResponseEntity<UserSolutionScoreDto>> addSolution(
            @Valid @RequestBody UserSolutionDto userSolutionDto) {

        return userSolutionService.addSolution(
                        userSolutionDto.getUserId(),
                        userSolutionDto.getChallengeId(),
                        userSolutionDto.getLanguageId(),
                        userSolutionDto.getSolutionText())
                .map(savedScoreDto ->
                        ResponseEntity.status(HttpStatus.ACCEPTED).body(savedScoreDto)
                );
    }


    @GetMapping(path = "/statistics/percent/{idChallenge}")
    @Operation(
            summary = "Percentage for a challenge idChallenge when users challengeUserStatus is not empty(started and ended in solutionUser) .",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Float.class),
                            mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Something went wrong",
                            content = {@Content(schema = @Schema())})
            }
    )
    public Mono<Float> challengeUserPercentageStatistic(
            @PathVariable("idChallenge")
            @GenericUUIDValid(message = "Invalid UUID for challenge")
            String idChallenge) {

        return serviceChallengeStatistics.getChallengeUsersPercentage(UUID.fromString(idChallenge));
    }


    @PutMapping("/bookmark")
    @Operation(
            summary = "Mark or create a bookmark",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bookmark marked or created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public Mono<ResponseEntity<BookmarkRequestDto>> markOrAddBookmark(

            @Valid @RequestBody BookmarkRequestDto bookmarkRequestDto) {

        return userSolutionService.markAsBookmarked(

                        bookmarkRequestDto.getUuid_challenge(),
                        bookmarkRequestDto.getUuid_language(),
                        bookmarkRequestDto.getUuid_user(),
                        bookmarkRequestDto.isBookmarked())
                .map(result -> ResponseEntity.ok(bookmarkRequestDto));
    }
}