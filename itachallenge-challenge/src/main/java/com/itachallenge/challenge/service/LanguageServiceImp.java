package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.exception.NotFoundException;
import com.itachallenge.challenge.helper.SafeDataParse;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
public class LanguageServiceImp implements LanguageService {

    private static final String LANGUAGE_NOT_FOUND = "Language with id %s not found";

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Override
    public Flux<ChallengeDocument> filterByLanguage(Flux<ChallengeDocument> challenges, Optional<String> idLanguage) {
        if (idLanguage.isPresent() && !idLanguage.get().isBlank()) {
            UUID finalLanguageUUID;
            try {
                finalLanguageUUID = UUID.fromString(idLanguage.get());
            } catch (IllegalArgumentException e) {
                return Flux.error(new IllegalArgumentException("El id del lenguaje no es un UUID válido"));
            }

            return challenges.filter(challenge -> {
                if (challenge.getLanguages() == null || challenge.getLanguages().isEmpty()) {
                    return false;
                }

                boolean match = challenge.getLanguages().stream()
                        .filter(Objects::nonNull)
                        .peek(lang -> System.out.printf(
                                "Reto '%s': comparando %s con %s%n",
                                challenge.getTitle(),
                                lang.getIdLanguage(),
                                finalLanguageUUID
                        ))
                        .map(LanguageDocument::getIdLanguage)
                        .filter(Objects::nonNull)
                        .anyMatch(langId -> langId.equals(finalLanguageUUID));

                return match;
            });
        }
        return challenges;
    }




}









