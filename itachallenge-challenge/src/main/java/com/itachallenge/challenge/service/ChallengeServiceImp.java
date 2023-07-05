package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.RelatedDto;
import com.itachallenge.challenge.exception.ErrorResponseMessage;
import com.itachallenge.challenge.helper.Converter;
import com.itachallenge.challenge.repository.ChallengeRepository;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChallengeServiceImp implements IChallengeService {
    /*
     TODO El método getChallengeId(UUID id) tiene código comentado para cuando la capa repositorio esté operativa, solo se deverán de descomentar.
     */

    //VARIABLES
    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

    @Autowired
    private ChallengeDto challengeDto;
   @Autowired
   ChallengeRepository challengeRepository;
  

    @Override
    public Mono<?> getChallengeId(UUID id) {
        challengeDto.setChallengeId(id); //ELIMINAR CON REPOSITORIO

        Mono<ChallengeDto> challenge = Mono.just(challengeDto); //ELIMINAR CON REPOSITORIO
        //Mono<Challenge> challenge = challengeRepository.findByUuid(id);

        ErrorResponseMessage errorMessage = new ErrorResponseMessage(HttpStatus.OK.value(), "Challenge not found.");

        return challenge
                //.map(challengeMapper::mapToChallengeDto)
                .map(dto -> ResponseEntity.ok().body((Object) dto))
                .switchIfEmpty(Mono.just(ResponseEntity.ok().body(errorMessage))); //ELIMINAR CON REPOSITORIO
                //.switchIfEmpty(Mono.defer(() -> Mono.just(ResponseEntity.ok().body(errorMessage))))
                //.map(ResponseEntity::getBody);
    }

    @Override //Comprueba si la UUID es valida.
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
	
			related = Mono.just(challengeRepository.findByUuid(UUID.fromString(challengeId))
					.block()
					.getRelatedChallenges()
					.stream()
					.map(id -> Converter.toRelatedDto(challengeRepository.findById(id).block()))
					.collect(Collectors.toList()));

		return related;
	}
}
