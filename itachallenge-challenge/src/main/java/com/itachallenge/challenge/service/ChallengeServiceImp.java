package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.RelatedDto;
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

	public Mono<List<RelatedDto>> getRelatedChallengePaginated(String id, int page, int size) {

		Mono<List<RelatedDto>> allRelatedChallenges = getRelatedChallenge(id);

		int startIndex = page * size;
		Flux<RelatedDto> subListChallenges = allRelatedChallenges.flatMapMany(Flux::fromIterable).skip(startIndex)
				.take(size);

		// Collect the subList challenges into a list
		return subListChallenges.collectList();
	}

	public Mono<List<RelatedDto>> getRelatedChallenge(String challengeId) {

		Mono<List<RelatedDto>> related = null;

		try {
			/*cuando funcione respository este metodo obtendrá el challengeDocument asociado a la id
			y de él extraerá una lista de UUID. El converter RelatedDto buscará en el repo 
			para cada UUID de la lista, el challengeDocument y extraerá el Título para generar el RelatedDto
			correspondiente. Después devolverá la lista de RelatedDto*/
			related = Mono.just(dataFeed(challengeId));

		} catch (Exception e) {

		}
		return related;
	}

//para pruebas antes de que funcione repositorio para obtener un challenge a partir de su UUID
	public List<RelatedDto> dataFeed(String id) {

		RelatedDto rel1 = RelatedDto.builder().relatedId(UUID.fromString("40728c9c-a557-4d12-bf8f-3747d0924197"))
				.titleRelatedId("titulo 1").build();
		RelatedDto rel2 = RelatedDto.builder().relatedId(UUID.fromString("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0"))
				.titleRelatedId("titulo 2").build();
		RelatedDto rel3 = RelatedDto.builder().relatedId(UUID.fromString("5f71e51d-1e3e-44a2-bc97-158021f1a344"))
				.titleRelatedId("titulo 3").build();

		List<RelatedDto> related = new ArrayList<>();

		related.add(rel1);
		related.add(rel2);
		related.add(rel3);

		return related;

	}

}
