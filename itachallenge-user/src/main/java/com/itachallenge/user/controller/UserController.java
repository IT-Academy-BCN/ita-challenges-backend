package com.itachallenge.user.controller;

import com.itachallenge.user.annotations.GenericUUIDValid;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import com.itachallenge.user.service.IUserSolutionService;
import com.itachallenge.user.service.ServiceChallengeStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(value = "/itachallenge/api/v1/user")
public class UserController {
    //region ATTRIBUTES
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    ServiceChallengeStatistics serviceChallengeStatistics;
    @Autowired
    private IUserSolutionService userScoreService;
    @Autowired
    private IUserSolutionRepository userScoreRepository;
    //endregion ATTRIBUTES


    //region ENDPOINTS
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
        //region VARIABLES
        Mono<List<ChallengeStatisticsDto>> elements = null;

        //endregion VARIABLES


        //region ACTIONS
        // INITIAL CHECKS
        if (!challengeIds.isEmpty()) {
            // CALL SERVICE METHOD
            elements = serviceChallengeStatistics.getChallengeStatistics(challengeIds);
        }

        //endregion ACTIONS

        // OUT
        return elements;

    }

    //endregion ENDPOINTS

    @GetMapping(path = "/solution/user/{idUser}/challenge/{idChallenge}/language/{idLanguage}")
    @Operation(
            summary = "obtains all the solutions to a challenge with the given language and user.",
            responses = {
                    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = SolutionUserDto.class), mediaType = "application/json") }),
                    @ApiResponse(responseCode = "400", description = "No user with the required id.", content = { @Content(schema = @Schema()) })
            }
    )
    public Mono<SolutionUserDto<UserScoreDto>> GetSolutionsByUserIdChallengeIdLanguageId(
            @PathVariable("idUser") @GenericUUIDValid(message = "Invalid UUID for user") String idUser,
            @PathVariable("idChallenge") @GenericUUIDValid(message =  "Invalid UUID for challenge") String idChallenge,
            @PathVariable("idLanguage") @GenericUUIDValid(message = "Invalid UUID for language") String idLanguage) {
        return userScoreService.getChallengeById(idUser, idChallenge, idLanguage);
    }
}