package com.itachallenge.challenge.controller;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.service.ChallengeServiceImp;
import com.itachallenge.challenge.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    private static final Logger log = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ChallengeServiceImp challengeService;

    @PostMapping("/new")
    public Mono<ResponseEntity<ResourceDto>> createResource(@RequestBody ResourceDto resourceDto) {
        return challengeService.findChallengesByTopic(resourceDto.getTopic())
                .collectList()
                .flatMap(challenges -> {
                    if (!challenges.isEmpty()) {
                        List<UUID> challengeIds = challenges.stream()
                                .map(ChallengeDto::getChallengeId)
                                .collect(Collectors.toList());
                        resourceDto.setChallengeIds(challengeIds);
                    } else {
                        resourceDto.setChallengeIds(new ArrayList<>());
                    }

                    return resourceService.createResource(resourceDto)
                            .map(savedResourceDto -> ResponseEntity
                                    .status(HttpStatus.CREATED)
                                    .body(savedResourceDto));
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
