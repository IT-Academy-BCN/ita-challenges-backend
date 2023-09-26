package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.document.SolutionDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.dto.SolutionDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
public class Converter {

    public Flux<ChallengeDto> fromChallengeToChallengeDto(Flux<ChallengeDocument> challengeFlux) {
        return challengeFlux.map(this::toChallengeDto);
    }

    public Flux<LanguageDto> fromLanguageToLanguageDto(Flux<LanguageDocument> languageFlux) {
        return languageFlux.map(this::toLanguageDto);
    }

    public Flux<SolutionDto> fromSolutionToSolutionDto(Flux<SolutionDocument> solutionFlux) {
        return solutionFlux.map(this::toSolutionDto);
    }

    private ChallengeDto toChallengeDto(ChallengeDocument challenge) {
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

    private LanguageDto toLanguageDto(LanguageDocument language) {
        return new LanguageDto(language.getIdLanguage(), language.getLanguageName());
    }

    private SolutionDto toSolutionDto(SolutionDocument solution) {
        return new SolutionDto(solution.getUuid(), solution.getSolutionText(), solution.getIdLanguage());
    }

    //metodo para formatear creationDate del document al formato requerido en el .json
    public String getFormattedCreationDateTime(LocalDateTime creationDateDocument) {

        ZoneId zoneId = ZoneId.of("Europe/Paris");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(creationDateDocument, zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return zonedDateTime.format(formatter);

    }

}
