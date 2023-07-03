package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import org.mapstruct.Mapper;

@Mapper
public interface ConverterMapStruct {

    LanguageDto LanguageDocToLanguageDto(LanguageDocument languageDocument);

    LanguageDocument LanguageDtoToLanguageDoc(LanguageDto languageDto);

    ChallengeDto ChallengeDocToChallengeDto(ChallengeDocument challengeDocument);

    ChallengeDocument ChallengeDtoToChallengeDoc(ChallengeDto challengeDto);

}
