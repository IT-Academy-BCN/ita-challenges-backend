package com.itachallenge.user.controller;

import com.itachallenge.user.config.PropertiesConfig;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.service.ServiceChallengeStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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

    //endregion ATTRIBUTES


    //region CONSTRUCTOR

    //endregion CONSTRUCTOR


    //region ENDPOINTS
    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA User!!!";
    }

    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "414", description = "URI too long")
    })
    @Operation(summary = "Get Basic Info of Challenge")
    @GetMapping(value = "/statistics")
    public Mono<List<ChallengeStatisticsDto>> GetBasicInfoChallenge(@RequestParam("challenge") List<UUID> challengeIds,
                                                                    HttpServletRequest request) {
        //region VARIABLES
        Mono<List<ChallengeStatisticsDto>> elements = null;
        //endregion VARIABLES


        //region ACTIONS
        // INITIAL CHECKS
        ValidateURLLength(request);
        if (!ValidateUUID(challengeIds)) {
            // CALL SERVICE METHOD
            elements = serviceChallengeStatistics.GetChallengeStatistics(challengeIds);
        }

        //endregion ACTIONS

        // OUT
        return elements;
    }

    //endregion ENDPOINTS


    //region METHODS: Private
    private void ValidateURLLength(HttpServletRequest requestIn) {
        if (PropertiesConfig.URLMAXLENGTH < requestIn.getRequestURL().length() + requestIn.getQueryString().length()) {
            throw new ResponseStatusException(HttpStatus.URI_TOO_LONG);
        }
    }

    private boolean ValidateUUID(List<UUID> challengeStaDtos) {
        return challengeStaDtos.isEmpty();
    }

    //endregion METHODS: Private


}

