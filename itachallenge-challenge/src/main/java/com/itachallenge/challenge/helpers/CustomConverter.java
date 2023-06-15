package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.ChallengeI;
import com.itachallenge.challenge.documents.LanguageI;
import com.itachallenge.challenge.dtos.ChallengeDto;
import com.itachallenge.challenge.dtos.LanguageDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
public class CustomConverter implements StarterConverter,
        ChallengeDocConverter, LanguageDocConverter {

    private ChallengeI challengeIDoc;

    private LanguageI languageIDoc;

    public ChallengeDocConverter from(ChallengeI challengeI) {
        this.challengeIDoc = challengeI;
        return this;
    }

    public LanguageDocConverter from(LanguageI languageI) {
        this.languageIDoc = languageI;
        return this;
    }

    public ChallengeDto toChallengeDto(Float percentage, Integer popularity) {
        return ChallengeDto.builder()
                .challengeId(challengeIDoc.getUuid())
                .level(challengeIDoc.getLevel())
                .title(challengeIDoc.getTitle())
                .languages(challengeIDoc.getLanguages().stream()
                        .map(languageI -> this.from(languageI).toLanguageDto())
                        .collect(Collectors.toSet()))
                .percentage(percentage)
                .popularity(popularity)
                .creationDate(getFormattedCreationDateTime(challengeIDoc.getCreationDate()))
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
        return new LanguageDto(languageIDoc.getIdLanguage(), languageIDoc.getLanguageName());
    }
}