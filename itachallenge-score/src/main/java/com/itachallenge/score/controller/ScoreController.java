package com.itachallenge.score.controller;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.zmq.TestingValuesResponseDto;
import com.itachallenge.score.service.IScoreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/score")
public class ScoreController {
    @Autowired
    IScoreService scoreService;
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

    @GetMapping("/zmq")
    public Mono<ResponseEntity<TestingValuesResponseDto>> getTestParams() {
        return Mono.just(ResponseEntity.ok(scoreService.getTestParams("dcacb291-b4aa-4029-8e9b-284c8ca80296")));
    }


}

