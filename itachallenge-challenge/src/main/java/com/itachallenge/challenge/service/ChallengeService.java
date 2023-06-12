package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
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
    public Flux<ChallengeDto> getAllRelatedsByUuid(UUID id) {

        return challengeRepository.findById(id)
                .map(challenge -> challenge.getRelatedChallenges())
                .flatMapMany(Flux::fromIterable)
                .flatMap(idRelated -> challengeRepository.findById(id))
                .map(challengeMapper::mapToChallengeDto)
                .switchIfEmpty(Flux.empty());
    }

    //RETORNA EN UN MONO TODOS LOS RELACIONADOS PAGINADOS
    public Mono<GenericResultDto<ChallengeDto>> getRelateds(UUID id, int offset, int limit){
        Flux<ChallengeDto> relatedsFlux = challengeRepository.findFirstByUuid(id)
                .map(challenge -> challenge.getRelatedChallenges())
                .flatMapMany(Flux::fromIterable)
                .flatMap(idRelated -> challengeRepository.findFirstByUuid(idRelated))
                .map(challengeMapper::mapToChallengeDto)
                .switchIfEmpty(Flux.empty());

        Mono<List<ChallengeDto>> listMono = relatedsFlux.collectList();

        List<ChallengeDto> listRelateds = listMono.block();

        return getResultDto(offset, limit ,listRelateds);
    }

    private Mono<GenericResultDto<ChallengeDto>> getResultDto(
            int offset, int limit, List<ChallengeDto> relatedsFilter) {

        int count = relatedsFilter.size();

        genericResultDto.setInfo(offset, limit, count, relatedsFilter);
        return Mono.just(genericResultDto);
    }

    public Mono<ChallengeDto> unMono(UUID id){
        return challengeRepository.findFirstByUuid(id)
                .map(challengeMapper::mapToChallengeDto);
    }
}
