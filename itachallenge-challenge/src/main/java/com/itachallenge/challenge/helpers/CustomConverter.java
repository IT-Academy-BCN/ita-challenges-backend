package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.documents.Language;
import com.itachallenge.challenge.dtos.ChallengeDto;
import com.itachallenge.challenge.dtos.LanguageDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CustomConverter implements StarterConverter,
        ChallengeDocConverter, LanguageDocConverter {

    private Challenge challengeDoc;

    private Language languageDoc;

    //private ChallengeDummy challengeDoc;
    //private LanguageDummy languageDoc;

    public ChallengeDocConverter from(Challenge challenge) {
        this.challengeDoc = challenge;
        return this;
    }

    public LanguageDocConverter from(Language language) {
        this.languageDoc = language;
        return this;
    }

    public ChallengeDto toChallengeDto(Float percentage, Integer popularity) {
        return ChallengeDto.builder()
                .challengeId(challengeDoc.getUuid())
                .level(challengeDoc.getLevel())
                .title(challengeDoc.getTitle())
                .languages(challengeDoc.getLanguages().stream()
                        .map(language -> this.from(language).toLanguageDto())
                        .collect(Collectors.toSet()))
                .percentage(percentage)
                .popularity(popularity)
                .creationDate(getFormattedCreationDateTime(challengeDoc.getCreationDate()))
                .build();
    }

    public LanguageDto toLanguageDto() {
        return fromLanguageToLanguageDto();
    }

    //metodo para formatear creationDate del document al formato requerido en el .json
    private String getFormattedCreationDateTime(LocalDateTime creationDateDocument) {

        ZoneId zoneId = ZoneId.of("Europe/Paris");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(creationDateDocument, zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return zonedDateTime.format(formatter);

    }

    private LanguageDto fromLanguageToLanguageDto(){
        return new LanguageDto(languageDoc.getIdLanguage(), languageDoc.getLanguageName());
    }
}