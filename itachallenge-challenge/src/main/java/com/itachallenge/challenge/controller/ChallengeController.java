package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dtos.RelatedDto;
import com.itachallenge.challenge.services.ChallengeServiceImp;

import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/challenge")
public class ChallengeController {

	@Autowired
	ChallengeServiceImp challengeService;

	@GetMapping(path = "/getOne/{id}/related")
	public Set<RelatedDto> relatedChallenge(@PathVariable("id") String id) throws Exception {
		try {

			// Service proveera el set de related correspondiente a la id del challenge en
			// el codigo final
			Set<RelatedDto> related = challengeService.getRelatedChallenge(UUID.fromString(id));

			return related;

		} catch (Exception e) {
			throw new Exception("An error occurred while accesing database");

		}

	}
}