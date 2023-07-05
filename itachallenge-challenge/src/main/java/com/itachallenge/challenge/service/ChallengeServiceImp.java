package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.exception.ErrorResponseMessage;
import com.itachallenge.challenge.helper.Converter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.LanguageRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ChallengeServiceImp implements IChallengeService {
  
    //VARIABLES
    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

    @Autowired
    private ChallengeDto challengeDto;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private Converter converter;


    @Override
    public Mono<ChallengeDto> getChallengeId(UUID id) {
        return Mono.from(converter.fromChallengeToChallengeDto(Flux.from(challengeRepository.findByUuid(id))));
    }

    @Override //Comprueba si la UUID es valida.
    public boolean isValidUUID(String id) {
        return !StringUtils.isEmpty(id) && UUID_FORM.matcher(id).matches();
    }

    @Override
    public Flux<ChallengeDto> getChallenges () {
        Flux<ChallengeDocument> challengesList = challengeRepository.findAll();
        return converter.fromChallengeToChallengeDto(challengesList);
    }




}
