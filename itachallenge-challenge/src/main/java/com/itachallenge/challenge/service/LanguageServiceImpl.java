package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
public class LanguageServiceImpl implements LanguageService {

    private static final String LANGUAGE_NOT_FOUND = "Language with id %s not found";

    @Autowired
    private DocumentToDtoConverter<LanguageDocument, LanguageDto> languageConverter = new DocumentToDtoConverter<>();

    @Autowired
    private LanguageRepository languageRepository;

    @Cacheable(value = "allLanguages")
    @Override
    public Mono<GenericResultDto<LanguageDto>> getAllLanguages() {
        Flux<LanguageDto> languagesDto = languageConverter.convertDocumentFluxToDtoFlux(languageRepository.findAll(), LanguageDto.class);
        return languagesDto.collectList().map(language -> {
            GenericResultDto<LanguageDto> resultDto = new GenericResultDto<>();
            resultDto.setInfo(0, language.size(), language.size(), language.toArray(new LanguageDto[0]));
            return resultDto;
        });
    }

    @Override
    public Mono<LanguageDocument> findByIdLanguage(UUID id){
        return languageRepository.findByIdLanguage(id);
    }

    public Mono<LanguageDocument> findFirstByLanguageName(String languageName) {
        return languageRepository.findFirstByLanguageName(languageName);
    }

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









