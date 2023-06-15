package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.document.ReadUuid;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.ReadUuidDto;
import com.itachallenge.challenge.helpers.ChallengeMapper;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ChallengeService{

    @Autowired
    ChallengeRepository challengeRepository;

    @Autowired
    private GenericResultDto<ChallengeDto> genericResultDto;

    @Autowired
    private ChallengeMapper challengeMapper;
    private static final Logger log = LoggerFactory.getLogger(ChallengeService.class);


    //RETORNA EN UN FLUX TODOS LOS RELACIONADOS SIN PAGINAR
/*    public Flux<ChallengeDto> getAllRelatedsByUuid(UUID id) {

        return challengeRepository.findById(id)
                .map(challenge -> challenge.getRelatedChallenges())
                .flatMapMany(Flux::fromIterable)
                .flatMap(idRelated -> challengeRepository.findById(idRelated))
                .map(challengeMapper::mapToChallengeDto)
                .switchIfEmpty(Flux.empty());
    }
*/
    //RETORNA EN UN MONO TODOS LOS RELACIONADOS PAGINADOS
    public Mono<GenericResultDto<ChallengeDto>> getRelateds(UUID id, int offset, int limit){

        Flux<ReadUuidDto> readUuidRelateds = arraysUuids(id);

        Flux<UUID> uuidRelateds = readUuidRelateds.map(uuids -> uuids.getUuid());

        Flux<ChallengeDto> relatedsFlux = uuidRelateds.flatMap(idRelated -> challengeRepository.findByUuid(idRelated))
                .map(challengeMapper::mapToChallengeDto)
                .switchIfEmpty(Flux.empty());

        List<ChallengeDto> relateds = relatedsFlux
                .toStream()
                .collect(Collectors.toList());

        return getResultDto(offset, limit ,relateds);
    }

    private Mono<GenericResultDto<ChallengeDto>> getResultDto(
            int offset, int limit, List<ChallengeDto> relatedsFilter) {

        int count = relatedsFilter.size();

        genericResultDto.setInfo(offset, limit, count, relatedsFilter);
        return Mono.just(genericResultDto);
    }

    public Mono<ChallengeDto> getOneChallenge(UUID id){
        return challengeRepository.findFirstByUuid(id)
                .map(challengeMapper::mapToChallengeDto);
    }

    public Flux<ChallengeDto> getAll(){
        return challengeRepository.findAll()
                .map(challengeMapper::mapToChallengeDto);
    }

    //RETORNA LOS OBJETOS UUID DE UN CHALLENGE A PARTIR DE ESE CHALLENGE
    public Flux<ReadUuidDto> arraysUuids(UUID id){
        Flux<ReadUuidDto> uuidRelateds = challengeRepository.findByUuid(id)
                .map(challenge -> challenge.getRelatedChallenges())
                .flatMapMany(Flux::fromIterable)
                .map(challengeMapper::read);

        return uuidRelateds;
    }
}
