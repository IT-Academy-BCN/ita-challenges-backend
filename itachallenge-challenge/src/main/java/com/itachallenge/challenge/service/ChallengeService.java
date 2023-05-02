package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.challengessection.ChallengesSectionInfoDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChallengeService {

    public Mono<ChallengesSectionInfoDto> getChallengesSectionInfo(){
        return Mono.just(ChallengesSectionInfoDto.withAllInfo());
    }
}
