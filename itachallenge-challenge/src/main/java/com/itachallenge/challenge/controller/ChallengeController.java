package com.itachallenge.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;


@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

    private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");


        Optional<URI> uri = discoveryClient.getInstances("itachallenge-challenge")
                .stream()
                .findAny()
                .map( s -> s.getUri());

        log.info("****** URI: " + uri.get().toString());

        Optional<String> services = discoveryClient.getServices()
                .stream()
                .findAny()
                .map( s -> s.toString());

        log.info("****** Services: " + services.get());
        return "Hello from ITA Challenge!!!";
    }

}
