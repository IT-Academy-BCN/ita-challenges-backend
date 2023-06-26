package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.RelatedDto;
import com.itachallenge.challenge.services.ChallengeServiceImp;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

	@Autowired
	ChallengeServiceImp challengeService;

  	  private static final Logger log = LoggerFactory.getLogger(ChallengeController.class);

  	  @Operation(summary = "Testing the App")
  	  @GetMapping(value = "/test")
  	  public String test() {
       		 log.info("** Saludos desde el logger **");
       		 return "Hello from ITA Challenge!!!";
    }

	@GetMapping(path = "/{id}/related")
	public Mono<ResponseEntity<Set<RelatedDto>>> relatedChallenge(@PathVariable("id") String id) {
	    return challengeService.getRelatedChallenge(UUID.fromString(id))
	            .map(ResponseEntity::ok)
	            .onErrorResume(e -> Mono.error(new Exception("An error occurred while accessing the database")));
	}

}