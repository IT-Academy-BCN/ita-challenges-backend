package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.config.PropertiesConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(value = "/itachallenge/api/v1/info")
public class InfoController {

    private static final String NO_SERVICE = "No Services";

    private static final Logger log = LoggerFactory.getLogger(InfoController.class);

    private DiscoveryClient discoveryClient;

    @Value("${spring.application.version}")
    private String version;

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");

        Optional<String> optChallengeService = discoveryClient.getInstances("itachallenge-challenge")
                .stream()
                .findAny()
                .map(Object::toString);

        Optional<String> userService = discoveryClient.getInstances("itachallenge-user")
                .stream()
                .findAny()
                .map(Object::toString);


        log.info("~~~~~~~~~~~~~~~~~~~~~~");
        log.info("Scanning micros:");

        StringBuilder logMessage = new StringBuilder("Scanning micros:");

        if (userService.isPresent()) {
            logMessage.append(System.lineSeparator()).append("User service available");
        } else {
            logMessage.append(System.lineSeparator()).append(NO_SERVICE);
        }

        if (optChallengeService.isPresent()) {
            logMessage.append(System.lineSeparator()).append("Challenge service available");
        } else {
            logMessage.append(System.lineSeparator()).append(NO_SERVICE);
        }


        String logMessageStr = logMessage.toString();
        log.info(logMessageStr);


        log.info("~~~~~~~~~~~~~~~~~~~~~~");


        return "Hello from ITA Challenge!!!";
    }

    @GetMapping("/version")
    @Operation(
            summary = "Get Application Version",
            description = "Retrieve the version of the application.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response with the application version and name.",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    public Mono<ResponseEntity<Map<String, String>>> getVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("application_name", appName);
        response.put("version", version);
        return Mono.just(ResponseEntity.ok(response));
    }

}
