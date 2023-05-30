package com.itachallenge.challenge.controller;


import com.itachallenge.challenge.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    private final ChallengeService challengeService;
    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);
    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge!!!";
    }

    @Operation(summary = "Deletes all resources given a id")
    @DeleteMapping("/resources/{resourceId}")
    public ResponseEntity<Void> deleteByResourceID(@PathVariable String resourceId){
        if(challengeService.removeResource(resourceId)){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }



}
