package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.RelatedDto;
import com.itachallenge.challenge.services.ChallengeServiceImp;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

	@Autowired
	ChallengeServiceImp challengeService;

	@GetMapping(path = "/getOne/{id}/related")
	public Mono<ResponseEntity<Set<RelatedDto>>> relatedChallenge(@PathVariable("id") String id) {
	    return challengeService.getRelatedChallenge(UUID.fromString(id))
	            .map(ResponseEntity::ok)
	            .onErrorResume(e -> Mono.error(new Exception("An error occurred while accessing the database")));
	}

}