package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.dummies.ChallengeDummy;
import com.itachallenge.challenge.documents.dummies.LanguageDummy;
import com.itachallenge.challenge.dtos.ChallengeBasicInfoDto;
import com.itachallenge.challenge.dtos.ChallengeDto;
import com.itachallenge.challenge.dtos.LanguageDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
class CustomConverter implements StarterConverter,
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

    protected LanguageDummy getLanguageDoc() {
        return languageDoc;
    }

    public LanguageDto toLanguageDto() {
        return fromLanguageToLanguageDto();
    }

    private ChallengeBasicInfoDto fromChallengeToChallengeBasicDto(Float percentage, Integer popularity) {
        return ChallengeBasicInfoDto.builder()
                .level(challengeDoc.getLevel())
                .title(challengeDoc.getTitle())
                .languages(challengeDoc.getLanguages().stream()
                        .map(languageDoc -> this.from(languageDoc).toLanguageDto())
                        .collect(Collectors.toSet()))
                .percentage(percentage)
                .popularity(popularity)
                //TODO: add creation date
                //.creationDate(challengeDoc.getCreationDate())
                .build();
    }

    private LanguageDto fromLanguageToLanguageDto(){
        return new LanguageDto(languageDoc.idLanguage(), languageDoc.getLanguageName());
    }
}