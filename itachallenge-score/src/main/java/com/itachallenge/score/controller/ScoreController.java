package com.itachallenge.score.controller;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/score")
public class ScoreController {

    @Value("${version}")
    private String version;

    private static final Logger log = LoggerFactory.getLogger(ScoreController.class);

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Score!!!";
    }
    @PostMapping(value = "/score")
    public Mono<ResponseEntity<ScoreResponse>> createScore(@RequestBody ScoreRequest scoreRequest) {
        return Mono.just(scoreRequest)
                .map(req -> {
                    ScoreResponse scoreResponse = new ScoreResponse();
                    scoreResponse.setUuidChallenge(req.getUuidChallenge());
                    scoreResponse.setUuidLanguage(req.getUuidLanguage());
                    scoreResponse.setSolutionText(req.getSolutionText());
                    scoreResponse.setScore(99);//TODO
                    return ResponseEntity.ok(scoreResponse);
                });
    }

    @GetMapping("/version")
    @Operation(summary = "Get Application Version")
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok("Application version: "+version);
    }

}

