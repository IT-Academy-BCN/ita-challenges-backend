package com.itachallenge.score.controller;

import com.itachallenge.score.service.CodeProcessingManager;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
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

@RestController
@RequestMapping(value = "/itachallenge/api/v1/score")
public class ScoreController {

    private static final Logger log = LoggerFactory.getLogger(ScoreController.class);

    @Autowired
    private CodeProcessingManager codeProcessingManager;

    @Value("${spring.application.version}")
    private String version;

    @Value("${spring.application.name}")
    private String appName;

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA Score!!!";
    }

    @GetMapping("/version")
    public Mono<ResponseEntity<Map<String, String>>> getVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("application_name", appName);
        response.put("version", version);
        return Mono.just(ResponseEntity.ok(response));
    }

    @PostMapping(value = "/score")
    public Mono<ResponseEntity<ScoreResponse>> createScore(@RequestBody ScoreRequest scoreRequest) {
        return Mono.just(scoreRequest)
                .map(req -> codeProcessingManager.processCode(req));
    }
}