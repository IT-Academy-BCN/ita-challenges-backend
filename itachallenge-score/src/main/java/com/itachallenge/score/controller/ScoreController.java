package com.itachallenge.score.controller;

import com.itachallenge.score.dto.ScoreRequest;
import com.itachallenge.score.dto.ScoreResponse;
import com.itachallenge.score.mqclient.ZMQClient;
import com.itachallenge.score.service.CodeProcessingManager;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/score")
public class ScoreController {

    @Autowired                                    //
    ZMQClient zmqClient;                         //TODO delete after manual testing!

    private static final Logger log = LoggerFactory.getLogger(ScoreController.class);

    private CodeProcessingManager codeProcessingManager;

    @Value("${spring.application.version}")
    private String version;

    @Value("${spring.application.name}")
    private String appName;

    public ScoreController(CodeProcessingManager codeProcessingManager) {
        this.codeProcessingManager = codeProcessingManager;
    }

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Score!!!";
    }

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test-zmq")
    public String testzmq() {                             //TODO method delete after manual testing!
        log.info("** testing zmq **");

        UUID challengeId = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
        UUID solutionId = UUID.fromString("c8a5440d-6466-463a-bccc-7fefbe9396e4");

        zmqClient.requestTestParams(challengeId, solutionId);
        return "Hello from ITA Score!!!";
    }

    @GetMapping("/version")
    public Mono<ResponseEntity<Map<String, String>>> getVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("application_name", appName);
        response.put("version", version);
        return Mono.just(ResponseEntity.ok(response));
    }


    @Operation(summary = "Endpoint to execute the code and calculate the score")
    @PostMapping(value = "/score")
    public Mono<ResponseEntity<ScoreResponse>> createScore(@RequestBody ScoreRequest scoreRequest) {
        return Mono.just(scoreRequest)
                .map(req -> codeProcessingManager.processCode(req));
    }
}