package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.helper.ChallengeMapper;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class ChallengeServiceImp implements ChallengeService{

    private static final Logger log = LoggerFactory.getLogger(ChallengeServiceImp.class);

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChallengeMapper challengeMapper;

    @Autowired
    private GenericResultDto<ChallengeDto> genericResultDto;


    //RETORNA EN UN FLUX TODOS LOS RELACIONADOS SIN PAGINAR
    @Override
    public Flux<ChallengeDto> getAllRelatedsByUuid(UUID id) {
        return challengeRepository.findFirstByUuid(id)
                .map(challenge -> challenge.getRelatedChallenges())
                .flatMapMany(Flux::fromIterable)
                .flatMap(idRelated -> challengeRepository.findFirstByUuid(idRelated))
                .map(challengeMapper::mapToChallengeDto)
                .switchIfEmpty(Flux.empty());
    }

    //RETORNA EN UN MONO TODOS LOS RELACIONADOS PAGINADOS PERO CON BLOQUEO
    @Override
    public Mono<GenericResultDto<ChallengeDto>> getRelateds(UUID id, int offset, int limit){
        Flux<ChallengeDto> relatedsFlux = challengeRepository.findFirstByUuid(id)
                .map(challenge -> challenge.getRelatedChallenges())
                .flatMapMany(Flux::fromIterable)
                .flatMap(idRelated -> challengeRepository.findFirstByUuid(idRelated))
                .map(challengeMapper::mapToChallengeDto)
                .switchIfEmpty(Flux.empty());

        List<ChallengeDto> relateds = relatedsFlux.collectList().block();

        return getResultDto(offset, limit ,relateds);
    }

    private Mono<GenericResultDto<ChallengeDto>> getResultDto(
            int offset, int limit, List<ChallengeDto> relatedsFilter) {

        int count = relatedsFilter.size();

        genericResultDto.setInfo(offset, limit, count, relatedsFilter);
        return Mono.just(genericResultDto);
    }

}
