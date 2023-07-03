package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.exception.ErrorResponseMessage;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ChallengeServiceImp implements IChallengeService {
	/*
	 * TODO El método getChallengeId(UUID id) tiene código comentado para cuando la
	 * capa repositorio esté operativa, solo se deverán de descomentar.
	 */

	// VARIABLES
	private static final Pattern UUID_FORM = Pattern
			.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

	@Autowired
	private ChallengeDto challengeDto;

	@Override
	public Mono<?> getChallengeId(UUID id) {
		challengeDto.setChallengeId(id); // ELIMINAR CON REPOSITORIO

		Mono<ChallengeDto> challenge = Mono.just(challengeDto); // ELIMINAR CON REPOSITORIO
		// Mono<Challenge> challenge = challengeRepository.findByUuid(id);

		ErrorResponseMessage errorMessage = new ErrorResponseMessage(HttpStatus.OK.value(), "Challenge not found.");

		return challenge
				// .map(challengeMapper::mapToChallengeDto)
				.map(dto -> ResponseEntity.ok().body((Object) dto))
				.switchIfEmpty(Mono.just(ResponseEntity.ok().body(errorMessage))); // ELIMINAR CON REPOSITORIO
		// .switchIfEmpty(Mono.defer(() ->
		// Mono.just(ResponseEntity.ok().body(errorMessage))))
		// .map(ResponseEntity::getBody);
	}

	@Override // Comprueba si la UUID es valida.
	public boolean isValidUUID(String id) {
		return !StringUtils.isEmpty(id) && UUID_FORM.matcher(id).matches();
	}

	public Mono<List<UUID>> getRelatedChallengePaginated(String id, int page, int size) {

		Mono<List<UUID>> allRelatedChallenges = getRelatedChallenge(id);

		int startIndex = page * size;
		Flux<UUID> subListChallenges = allRelatedChallenges.flatMapMany(Flux::fromIterable).skip(startIndex).take(size);

		// Collect the subList challenges into a list
		return subListChallenges.collectList();
	}

	public Mono<List<UUID>> getRelatedChallenge(String challengeId) {

		Mono<List<UUID>> related = null;

		try {

			related = Mono.just(dataFeed(challengeId).getRelatedChallenges());

		} catch (Exception e) {

		}
		return related;
	}

//para pruebas antes de que funcione repositorio
	public ChallengeDocument dataFeed(String id) {

		UUID rel1 = UUID.fromString("40728c9c-a557-4d12-bf8f-3747d0924197");
		UUID rel2 = UUID.fromString("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0");
		UUID rel3 = UUID.fromString("5f71e51d-1e3e-44a2-bc97-158021f1a344");

		List<UUID> related = new ArrayList<>();

		related.add(rel1);
		related.add(rel2);
		related.add(rel3);

		ChallengeDocument challengeDoc = ChallengeDocument.builder().uuid(UUID.fromString(id)).level("2")
				.title("the one").languages(null).creationDate(null).relatedChallenges(related).solutions(null)
				.detail(null).resources(null).build();

		return challengeDoc;

	}

}
