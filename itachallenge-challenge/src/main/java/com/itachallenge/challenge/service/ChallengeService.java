package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.helpers.ChallengeMapper;
import com.itachallenge.challenge.repository.ChallengeRepository;
//import com.mongodb.reactivestreams.client.MongoClient;
//import com.mongodb.reactivestreams.client.MongoClients;
//import com.mongodb.reactivestreams.client.MongoCollection;
//import com.mongodb.reactivestreams.client.MongoDatabase;
//import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
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
    public Flux<ChallengeDto> getAllRelatedsByUuid(String id) {

        return challengeRepository.findByUuid(id)
                .map(challenge -> challenge.getRelatedChallenges())
                .flatMapMany(Flux::fromIterable)
                .flatMap(idRelated -> challengeRepository.findByUuid(id))
                .map(challengeMapper::mapToChallengeDto)
                .switchIfEmpty(Flux.empty());
    }

    //RETORNA EN UN MONO TODOS LOS RELACIONADOS PAGINADOS
    public Mono<GenericResultDto<ChallengeDto>> getRelateds(String id, int offset, int limit){
        Flux<ChallengeDto> relatedsFlux = challengeRepository.findByUuid(id)
                .map(challenge -> challenge.getRelatedChallenges())
                .flatMapMany(Flux::fromIterable)
                .flatMap(idRelated -> challengeRepository.findByUuid(idRelated))
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


    public Flux<ChallengeDto> getAll() {
        return challengeRepository.findAll()
                .map(challengeMapper::mapToChallengeDto);
    }


    public Mono<ChallengeDto> save(ChallengeDto challengeDto) {
        Challenge challenge = challengeMapper.mapToChallenge(challengeDto);

        Set<String> relateds = new HashSet<>();
        relateds.add("eb7a15e4-499e-481d-a909-6acc642b0c09");
        relateds.add("c0c7510b-f867-4375-b46f-0c164f30429f");

        challenge.setUuid("462d7617-477d-497f-8bd2-2fa5d897f20f");
        challenge.setRelatedChallenges(relateds);

        return challengeRepository.save(challenge)
                .map(c -> {
                    challengeDto.setUuid(c.getUuid());
                    return challengeDto;
                });
    }


    public Mono<ChallengeDto> getChallengeId(String id) {
        Mono<Challenge> challenge = challengeRepository.findById(id);
        //control
        log.info("Object challenge in getChallengeId(): " + challenge.blockOptional().isPresent());
        // Comprueba si existe el desafío
        return challenge.map(challengeMapper::mapToChallengeDto).switchIfEmpty(Mono.error(new IllegalArgumentException("ID challenge: " + id + " does not exist in the database.")));
    }

    public boolean isValidUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
