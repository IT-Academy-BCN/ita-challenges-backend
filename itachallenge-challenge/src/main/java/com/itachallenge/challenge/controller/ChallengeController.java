package com.itachallenge.challenge.controller;


import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.ReadUuidDto;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    @Autowired
    ChallengeService challengeService;

    @Autowired
    ChallengeRepository repository;

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge!!!";
    }

    //RETORNA EN UN MONO TODOS LOS RELACIONADOS PAGINADOS
 /*   @Operation(summary = "Returns all the challenges related to the chosen challenge according to its id")
    @GetMapping(value = "/{id}/related")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "503", description = "Service Unavailable") })
    Mono<GenericResultDto<ChallengeDto>> findAllRelatedsByUuid(
            @PathVariable String id,
            @RequestParam(required = false) String offset,
            @RequestParam(required = false) String limit){

        //Excepciones Si no encuentra el Id, si el id es válido
        //Si no tiene challenges
        return challengeService.getRelateds(UUID.fromString(id), this.getValidOffset(offset), this.getValidLimit(limit));
    }*/
    @GetMapping(value = "/{id}/related")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "503", description = "Service Unavailable") })
    Mono<GenericResultDto<ChallengeDto>> findAllRelatedsByUuid(
            @PathVariable String id,
            @RequestParam(required = false) String offset,
            @RequestParam(required = false) String limit) throws JSONException {

        //Excepciones Si no encuentra el Id, si el id es válido
        //Si no tiene challenges
        return challengeService.getRelatedsDummy();

    }


    private int getValidOffset(String offset) {
        if (offset == null || offset.isEmpty()) {
            return 0;
        }
        // NumberUtils.isDigits returns false for negative numbers
        if (!NumberUtils.isDigits(offset)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return Integer.parseInt(offset);
    }

    private int getValidLimit(String limit) {
        if (limit == null || limit.isEmpty() || limit.equals("-1")) {
            return -1;
        }
        // NumberUtils.isDigits returns false for negative numbers
        if (!NumberUtils.isDigits(limit)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return Integer.parseInt(limit);
    }

    @GetMapping(value = "/{id}")
    public Mono<ChallengeDto> OneChallenge(@PathVariable String id){
        return challengeService.getOneChallenge(UUID.fromString(id));
    }

    @GetMapping
    public Flux<ChallengeDto> allChallenges(){
        return challengeService.getAll();
    }

}
