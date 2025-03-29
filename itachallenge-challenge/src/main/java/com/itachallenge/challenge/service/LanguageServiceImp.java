package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.exception.NotFoundException;
import com.itachallenge.challenge.helper.SafeDataParse;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Service
public class LanguageServiceImp implements LanguageService {

    private static final String LANGUAGE_NOT_FOUND = "Language with id %s not found";

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Override
    public Flux<ChallengeDocument> filterByLanguage(Optional<String> idLanguageOptional) {
        return SafeDataParse.safeParseUUID(idLanguageOptional.orElse(null))
                .map(idLanguage ->
                        languageRepository.findByIdLanguage(idLanguage)
                                .switchIfEmpty(Mono.error(new NotFoundException(String.format(LANGUAGE_NOT_FOUND, idLanguage))))
                                .flatMapMany(language -> challengeRepository.findByLanguages_IdLanguage(idLanguage))
                )
                .orElseGet(challengeRepository::findAllByUuidNotNullExcludingTestingValues);
    }
}

