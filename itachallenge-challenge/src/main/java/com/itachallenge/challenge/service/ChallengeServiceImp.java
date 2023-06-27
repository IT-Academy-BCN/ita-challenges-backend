package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.RelatedDto;
import com.itachallenge.challenge.exception.ErrorResponseMessage;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ChallengeServiceImp implements IChallengeService {
    /*
     TODO El método getChallengeId(UUID id) tiene código comentado para cuando la capa repositorio esté operativa, solo se deverán de descomentar.
     */

    //VARIABLES
    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

    @Autowired
    private ChallengeDto challengeDto;

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
    @Override
	public Mono<Set<RelatedDto>> getRelatedChallenge(UUID challengeId) {
		
		//probablemente usemos el metodo getChallenge del Service para extraer el set del challenge concreto
		//esto nos devolverá el set de related o si no, alguno de los errores previstos en ese metodo
    	RelatedDto rel1 = new RelatedDto("40728c9c-a557-4d12-bf8f-3747d0924197");
		RelatedDto rel2 = new RelatedDto("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0");
		RelatedDto rel3 = new RelatedDto("5f71e51d-1e3e-44a2-bc97-158021f1a344");
    	
    	Set<RelatedDto> related = new LinkedHashSet<>();
		
		related.add(rel1);
		related.add(rel2);
		related.add(rel3);

		return Mono.just(related);
	}

}
