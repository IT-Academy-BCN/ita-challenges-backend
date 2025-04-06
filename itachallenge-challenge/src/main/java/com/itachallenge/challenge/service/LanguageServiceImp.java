package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
public class LanguageServiceImp implements LanguageService {

    private static final String LANGUAGE_NOT_FOUND = "Language with id %s not found";


    @Override
    public Flux<ChallengeDocument> filterByLanguage(Flux<ChallengeDocument> challenges, Optional<String> idLanguage) {
        if (challenges == null) {
            return Flux.empty();
        }

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









