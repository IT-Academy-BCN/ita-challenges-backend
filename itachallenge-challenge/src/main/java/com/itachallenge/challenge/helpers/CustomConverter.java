package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.dummies.ChallengeDummy;
import com.itachallenge.challenge.documents.dummies.LanguageDummy;
import com.itachallenge.challenge.dtos.ChallengeBasicInfoDto;
import com.itachallenge.challenge.dtos.ChallengeDto;
import com.itachallenge.challenge.dtos.LanguageDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class CustomConverter implements StarterConverter,
        ChallengeDocConverter, LanguageDocConverter {

    private ChallengeDummy challengeDoc;

    private LanguageDummy languageDoc;

    public ChallengeDocConverter from(ChallengeDummy challenge) {
        this.challengeDoc = challenge;
        return this;
    }

    public LanguageDocConverter from(LanguageDummy language) {
        this.languageDoc = language;
        return this;
    }

    public ChallengeDto toChallengeDtoWithOnlyBasicInfo(Float percentage, Integer popularity) {
        return ChallengeDto.builder(challengeDoc.getUuid())
                .basicInfo(fromChallengeToChallengeBasicDto(percentage,popularity))
                .build();
    }

    public LanguageDto toLanguageDto() {
        return fromLanguageToLanguageDto();
    }

    private ChallengeBasicInfoDto fromChallengeToChallengeBasicDto(Float percentage, Integer popularity) {
        return ChallengeBasicInfoDto.builder()
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

    //metodo para formatear creationDate del document al formato requerido en el .json
    private String getFormattedCreationDateTime(LocalDateTime creationDateDocument) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.forLanguageTag("es-ES"));
        return creationDateDocument.format(formatter);

    }

    private LanguageDto fromLanguageToLanguageDto(){
        return new LanguageDto(languageDoc.getIdLanguage(), languageDoc.getLanguageName());
    }
}