package com.itachallenge.user.controller;

import com.itachallenge.user.annotations.GenericUUIDValid;
import com.itachallenge.user.dtos.*;
import com.itachallenge.user.service.IServiceChallengeStatistics;
import com.itachallenge.user.service.IUserSolutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.*;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    IServiceChallengeStatistics serviceChallengeStatistics;
    @Autowired
    private IUserSolutionService userScoreService;

    @Value("${spring.application.version}")
    private String version;

    @Value("${spring.application.name}")
    private String appName;


    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA User!!!";
    }

    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "414", description = "URI too long")
    @Operation(summary = "Get Basic Info of Challenge")
    @GetMapping(value = "/statistics")
    public Mono<List<ChallengeStatisticsDto>> getChallengeStatistics(@RequestParam("challenge") List<UUID> challengeIds) {
        Mono<List<ChallengeStatisticsDto>> elements = null;

        if (!challengeIds.isEmpty()) {
            elements = serviceChallengeStatistics.getChallengeStatistics(challengeIds);
        }

        return elements;
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
        return userScoreService.getChallengeById(idUser, idChallenge, idLanguage);
    }

    @PutMapping(path = "/solution")
    @Operation(
            summary = "perform a solution, adding challenge,language,user, status and the corresponding solution text.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = SolutionUserDto.class),
                            mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "500", description = "Challenge status: ended",
                            content = {@Content(schema = @Schema())})
            }
    )
    public Mono<ResponseEntity<UserSolutionScoreDto>> addSolution(
            @Valid @RequestBody UserSolutionDto userSolutionDto) {

        return userScoreService.addSolution(userSolutionDto)
                .map(savedUserSolutionScoreDto ->
                        ResponseEntity.status(HttpStatus.OK).body(savedUserSolutionScoreDto)
                );
    }

    @GetMapping(value = "/bookmarks/{idChallenge}")
    @Operation(
            summary = "Get the count of bookmarks for a specific challenge.",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Long.class), mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "Challenge not found", content = {@Content(schema = @Schema())})
            }
    )
    public Mono<ResponseEntity<Map<String, Long>>> getBookmarkCountByIdChallenge(
            @PathVariable("idChallenge") @GenericUUIDValid(message = "Invalid UUID for challenge") String idChallenge) {
        return serviceChallengeStatistics.getBookmarkCountByIdChallenge(UUID.fromString(idChallenge))
                .map(count -> ResponseEntity.ok(Collections.singletonMap("bookmarked", count)));
    }

    @GetMapping(path = "/statistics/percent/{idChallenge}")
    @Operation(
            summary = "Percentage for a challenge idChallenge when users challengeUserStatus is not empty(started and ended in solutionUser) .",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Float.class),
                            mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = "Something went wrong",
                            content = {@Content(schema = @Schema())}),
                    @ApiResponse(responseCode = "404", description = "Challenge not found",
                            content = {@Content(schema = @Schema())})
            }
    )
    public Mono<ResponseEntity<ChallengeUserPercentageStatisticDto>> challengeUserPercentageStatistic(
            @PathVariable("idChallenge")
            @GenericUUIDValid(message = "Invalid UUID for challenge")
            String idChallenge) {

        return serviceChallengeStatistics.getChallengeUsersPercentage(UUID.fromString(idChallenge))
                .map(percentage -> new ChallengeUserPercentageStatisticDto(UUID.fromString(idChallenge), percentage))
                .map(ResponseEntity::ok);
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

        return userScoreService.markAsBookmarked(

                        bookmarkRequestDto.getUuid_challenge(),
                        bookmarkRequestDto.getUuid_language(),
                        bookmarkRequestDto.getUuid_user(),
                        bookmarkRequestDto.isBookmarked())
                .map(result -> ResponseEntity.ok(bookmarkRequestDto));
    }

    @GetMapping("/version")
    @Operation(
            summary = "Get Application Version",
            description = "Retrieve the version of the application.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response with the application version and name.",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    public Mono<ResponseEntity<Map<String, String>>> getVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("application_name", appName);
        response.put("version", version);
        return Mono.just(ResponseEntity.ok(response));
    }


    @GetMapping(path = "/{idUser}/challenges/solutions")
    @Operation(
            summary = "Retrieves all user challenges solutions and their status.",
            description = "Retrieves all user-contributed solutions for all challenges and their status (whether they've finished completing them or not).",
            responses = {
            @ApiResponse(responseCode = "200", description = "Challenges retrieved successfully", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = UserSolutionDto.class)), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid UUID for user"),
            @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public Mono<ResponseEntity<List<UserSolutionDto>>> getAllSolutionsByIdUser(
            @PathVariable("idUser") @GenericUUIDValid(message = "Invalid UUID for user") String idUser) {
        UUID userUuid = UUID.fromString(idUser);

        return userScoreService.showAllUserSolutions(userUuid)
                .collectList()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/{idUser}/challenge/{idChallenge}/solution/{idSolution}/score")
    @Operation(summary = "prepare json file for send",
            description = "phase 1 returns solToSend -> To Do: phase 2 we receive score value from ita-score server",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful response with the score value", content = {@Content(schema = @Schema(implementation = UserSolScoreDto.class), mediaType = "application/json")}),
                @ApiResponse(responseCode = "400", description = "Bad Request", content = {@Content(schema = @Schema())}),
                @ApiResponse(responseCode = "404", description = "Combination of User Id and Challenge Id provided not found", content = {@Content(schema = @Schema())}),
    })
    public Flux<ResponseEntity<UserSolScoreDto>> getScoreFromMicroScore(
            @PathVariable("idUser") @GenericUUIDValid(message = "Invalid UUID for user") String idUser,
            @PathVariable("idChallenge") @GenericUUIDValid(message = "Invalid UUID for challenge") String idChallenge,
            @PathVariable("idSolution") @GenericUUIDValid(message = "Invalid UUID for solution") String idSolution)
    {
        return this.userScoreService.getScore(idUser, idChallenge, idSolution)
                .map(userSolScoreDto -> ResponseEntity.status(HttpStatus.OK).body(userSolScoreDto));
    }
}