package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.document.Language;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
public class Converter {

    public Flux<ChallengeDto> fromChallengeToChallengeDto(Flux<Challenge> challengeFlux) {
        return challengeFlux.map(this::toChallengeDto);
    }

    public Flux<LanguageDto> fromLanguageToLanguageDto(Flux<Language> languageFlux) {
        return languageFlux.map(this::toLanguageDto);
    }

    private ChallengeDto toChallengeDto(Challenge challenge) {
        return ChallengeDto.builder()
                .challengeId(challenge.getUuid())
                .level(challenge.getLevel())
                .title(challenge.getTitle())
                .languages(challenge.getLanguages().stream()
                        .map(this::toLanguageDto)
                        .collect(Collectors.toSet()))
                .creationDate(getFormattedCreationDateTime(challenge.getCreationDate()))
                .build();
    }

    private LanguageDto toLanguageDto(Language language) {
        return new LanguageDto(language.getIdLanguage(), language.getLanguageName());
    }

    //metodo para formatear creationDate del document al formato requerido en el .json
    private String getFormattedCreationDateTime(LocalDateTime creationDateDocument) {

        ZoneId zoneId = ZoneId.of("Europe/Paris");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(creationDateDocument, zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return zonedDateTime.format(formatter);

    }

}
