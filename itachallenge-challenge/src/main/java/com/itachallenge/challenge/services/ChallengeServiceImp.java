package com.itachallenge.challenge.services;

import com.itachallenge.challenge.dto.RelatedDto;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class ChallengeServiceImp implements IChallengeService {

	@Override
	public Mono<Set<RelatedDto>> getRelatedChallenge(UUID challenge_id) {
		
		//probablemente usemos el metodo getChallenge del Service para extraer el set del challenge concreto
		//esto nos devolverá el set de related o si no, alguno de los errores previstos en ese metodo
		LinkedHashSet<RelatedDto> related = new LinkedHashSet<>();
		RelatedDto rel1 = new RelatedDto("40728c9c-a557-4d12-bf8f-3747d0924197");
		RelatedDto rel2 = new RelatedDto("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0");
		RelatedDto rel3 = new RelatedDto("5f71e51d-1e3e-44a2-bc97-158021f1a344");
		related.add(rel1);
		related.add(rel2);
		related.add(rel3);

		return Mono.just(related);
	}

}