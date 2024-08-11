package com.itachallenge.score.controller;

import com.itachallenge.score.component.CodeExecutionService;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.docker.DockerContainerHelper;
import com.itachallenge.score.filter.Filter;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.testcontainers.containers.GenericContainer;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/score")
public class ScoreController {

    private static final Logger log = LoggerFactory.getLogger(ScoreController.class);

    @Autowired
    private Filter filterChain;

    @Autowired
    private CodeExecutionService codeExecutionService;

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

    @PostMapping(value = "/score")
    public Mono<ResponseEntity<ScoreResponse>> createScore(@RequestBody ScoreRequest scoreRequest) {
        return Mono.just(scoreRequest)
                .map(req -> {
                    String sourceCode = req.getSolutionText();

                    boolean isValid = filterChain.apply(sourceCode);

                    if (!isValid) {
                        ScoreResponse errorResponse = new ScoreResponse();
                        errorResponse.setUuidChallenge(req.getUuidChallenge());
                        errorResponse.setUuidLanguage(req.getUuidLanguage());
                        errorResponse.setSolutionText(req.getSolutionText());
                        errorResponse.setScore(0);
                        errorResponse.setCompilationMessage("The input contains a non-ASCII character");

                        return ResponseEntity.badRequest().body(errorResponse);
                    }

                    GenericContainer<?> sandbox = DockerContainerHelper.createJavaSandboxContainer();
                    ExecutionResultDto executionResult;
                    try {
                        sandbox.start(); // Ensure the container is started
                        DockerContainerHelper.copyFileToContainer(sandbox, sourceCode, "/home/sandbox/CodeToExecute.java");
                        DockerContainerHelper.executeCommand(sandbox, "javac", "/home/sandbox/CodeToExecute.java");
                        DockerContainerHelper.executeCommand(sandbox, "java", "-cp", "/home/sandbox", "CodeToExecute");

                        executionResult = new ExecutionResultDto(/* populate with actual results */);
                    } catch (IOException | InterruptedException e) {
                        log.error("Error during code execution", e); // Log the error
                        executionResult = new ExecutionResultDto();
                        executionResult.setMessage("Error during code execution: " + e.getMessage());
                    } finally {
                        sandbox.stop(); // Ensure the container is stopped
                    }

                    int score = codeExecutionService.calculateScore(executionResult);

                    ScoreResponse scoreResponse = new ScoreResponse();
                    scoreResponse.setUuidChallenge(req.getUuidChallenge());
                    scoreResponse.setUuidLanguage(req.getUuidLanguage());
                    scoreResponse.setSolutionText(req.getSolutionText());
                    scoreResponse.setScore(score);
                    scoreResponse.setCompilationMessage(executionResult.getMessage());

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
