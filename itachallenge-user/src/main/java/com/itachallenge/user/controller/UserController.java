package com.itachallenge.user.controller;

import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.hash.UserHash;
import com.itachallenge.user.service.ServiceChallengeStatistics;
import com.itachallenge.user.service.UserHashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/user")
public class UserController {
    //region ATTRIBUTES
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    ServiceChallengeStatistics serviceChallengeStatistics;

    @Autowired
    UserHashService userHashService;

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

    @PostMapping("/hazRandom")
    public Mono<UserHash> hazrandom(){
        return userHashService.hazrandom();
    }

    @GetMapping("/dameuno/{id}")
    public Mono<UserHash> dameuno(@PathVariable String id){
        return userHashService.getone(UUID.fromString(id));
    }

    @GetMapping("/dametodo")
    public Flux<UserHash> dametodo(){
        return userHashService.getall();
    }

    @DeleteMapping("/borrauno/{id}")
    public Mono<Void> borraUno(@PathVariable String id){
        return userHashService.borraUno(UUID.fromString(id));
    }

    //endregion ENDPOINTS

}

