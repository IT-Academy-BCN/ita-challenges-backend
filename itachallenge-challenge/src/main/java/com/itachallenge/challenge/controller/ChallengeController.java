package com.itachallenge.challenge.controller;


import com.itachallenge.challenge.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);
    private final ChallengeService challengeService;
    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Challenge!!!";
    }


    @Operation(summary = "Deletes a resource giving a Resource ID")
    @DeleteMapping("/resource/{resourceId}")
    public ResponseEntity<String> deleteByResourceId(@PathVariable Long resourceId){
        if(challengeService.deleteByResourceId(resourceId)){
            return ResponseEntity.status(204).build();
        }else {
            return ResponseEntity.status(404).body("Not Found");
        }
    }


}
