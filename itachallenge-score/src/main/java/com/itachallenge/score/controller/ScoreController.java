package com.itachallenge.score.controller;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.zmq.ChallengeTestingValuesRequestDto;
import com.itachallenge.score.dto.zmq.TestingValuesDto;
import com.itachallenge.score.mqclient.ZMQClient;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/score")
public class ScoreController {

    private static final Logger log = LoggerFactory.getLogger(ScoreController.class);

    @Autowired
    ZMQClient zmqClient;
    @Autowired
    ChallengeTestingValuesRequestDto ctvRequestDto;

    @Value("${spring.application.version}")
    private String version;

    @Value("${spring.application.name}")
    private String appName;

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");

        ctvRequestDto.setChallengeId(UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296"));
        ctvRequestDto.setLanguageId(UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef"));

        zmqClient.sendMessage(ctvRequestDto, TestingValuesDto[].class)
                .thenAccept(response ->{
                        for(TestingValuesDto dto: (TestingValuesDto[]) response){
                        log.info("[ Response: " + dto.getInParam() + ", "
                                + dto.getOutParam() + " ]");
                        }
                })
                .exceptionally(e -> {
                    log.error(e.getMessage());
                    return null;
                });

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
    public Mono<ResponseEntity<Map<String, String>>> getVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("application_name", appName);
        response.put("version", version);
        return Mono.just(ResponseEntity.ok(response));
    }


}

