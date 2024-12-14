package com.itachallenge.user.controller;

import com.itachallenge.user.annotations.GenericUUIDValid;
import com.itachallenge.user.dtos.*;
import com.itachallenge.user.dtos.zmq.ScoreRequestDto;
import com.itachallenge.user.mqclient.ZMQClient;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.*;

import static java.util.Arrays.asList;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Mono.just;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

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
            elements = userScoreService.getChallengeStatistics(challengeIds);
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
            summary = "perform a solution, adding challenge, language, user, status and the corresponding solution text.",
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
        return userScoreService.getBookmarkCountByIdChallenge(UUID.fromString(idChallenge))
                .map(count -> ResponseEntity.ok(Collections.singletonMap("bookmarked", count)));
    }

    @GetMapping(path = "/statistics/percent/{idChallenge}")
    @Operation(
            summary = "Percentage for a challenge idChallenge when users challengeUserStatus is not empty (started and ended in solutionUser).",
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

        return userScoreService.getChallengeUsersPercentage(UUID.fromString(idChallenge))
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
        return just(ResponseEntity.ok(response));
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

    /**
     * Retrieves the statistics list for a specific user and language.
     *
     * <p><strong>Note:</strong> The path parameters {@code idUser} and {@code idLanguage}
     * are currently not utilized. The method returns a hardcoded {@link UserLanguageChallengesDto}
     * instance for demonstration purposes.</p>
     *
     * @param idUser     the unique identifier of the user
     * @param idLanguage the unique identifier of the language
     * @return a {@link Mono} containing the {@link UserLanguageChallengesDto} with hardcoded user language challenges data
     */
    @GetMapping(path = "/{idUser}/challenges/language/{idLanguage}/statistics/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserLanguageChallengesDto> getStatisticsListByIdUserAndIdLanguage(@PathVariable("idUser") String idUser, @PathVariable("idLanguage") String idLanguage) {

        return just(getUserLanguageChallengesDto());

    }


    /**
     * Creates a hardcoded {@link UserLanguageChallengesDto} instance.
     *
     * <p>This helper method currently does not utilize any input parameters and returns
     * predefined data. It serves as a placeholder for actual data retrieval logic.</p>
     *
     * @return a hardcoded {@link UserLanguageChallengesDto} object
     */
    private static UserLanguageChallengesDto getUserLanguageChallengesDto() {

        // Create Completed Challenges
        UserChallengeDto completedChallenge1 = UserChallengeDto.builder()
                .uuidChallenge("dcacb291-b4aa-4029-8e9b-284c8ca80296")
                .score(50)
                .build();

        UserChallengeDto completedChallenge2 = UserChallengeDto.builder()
                .uuidChallenge("f6e0f877-9560-4e68-bab6-7dd5f16b46a5")
                .score(50)
                .build();

        List<UserChallengeDto> completedChallenges = asList(completedChallenge1, completedChallenge2);

        // Create Saved Challenges
        UserChallengeDto savedChallenge1 = UserChallengeDto.builder()
                .uuidChallenge("dcacb291-b4aa-4029-8e9b-284c8ca80296")
                .build();

        UserChallengeDto savedChallenge2 = UserChallengeDto.builder()
                .uuidChallenge("f6e0f877-9560-4e68-bab6-7dd5f16b46a5")
                .build();

        UserChallengeDto savedChallenge3 = UserChallengeDto.builder()
                .uuidChallenge("9d2c4e2b-02af-4327-81b2-7dbf5c3f5a7d")
                .build();

        UserChallengeDto savedChallenge4 = UserChallengeDto.builder()
                .uuidChallenge("2f948de0-6f0c-4089-90b9-7f70a0812319")
                .build();

        UserChallengeDto savedChallenge5 = UserChallengeDto.builder()
                .uuidChallenge("a4b0f8d3-6571-4d8e-854d-ef93ea9b30a6")
                .build();

        List<UserChallengeDto> savedChallenges = asList(
                savedChallenge1,
                savedChallenge2,
                savedChallenge3,
                savedChallenge4,
                savedChallenge5
        );

        // Create ChallengesListsDto
        ChallengesListsDto challengesLists = ChallengesListsDto.builder()
                .completed(completedChallenges)
                .saved(savedChallenges)
                .build();

        // Create UserLanguageChallengesDto
        return UserLanguageChallengesDto.builder()
                .uuidUser("xxx")
                .uuidLanguage("xxxx")
                .challenges(challengesLists)
                .build();

    }

}