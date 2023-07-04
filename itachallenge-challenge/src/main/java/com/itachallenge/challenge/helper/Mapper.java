package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import org.mapstruct.Named;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@org.mapstruct.Mapper
public interface Mapper {

    ChallengeDto toDtoFromDoc(ChallengeDocument challengeFlux);

    LanguageDto toLanguageDtoFromLanguageDocument(LanguageDocument languageFlux);

    default Flux<ChallengeDto> toDtoFromDoc(Flux<ChallengeDocument> documentFlux) {
        return documentFlux.map(this::toDtoFromDoc);
    }

    default Flux<LanguageDto> toLanguageDtoFromLanguageDocument(Flux<LanguageDocument> languageFlux) {
        return languageFlux.map(this::toLanguageDtoFromLanguageDocument);
    }

    default String mapCreationDateToString(LocalDateTime localDateTime) {
        LocalDateTimeConverter converter = new LocalDateTimeConverter();
        return converter.getFormattedCreationDateTime(localDateTime);
    }

    default LocalDateTime mapStringToCreationDate(String dateString) {
        LocalDateTimeConverter converter = new LocalDateTimeConverter();
        return converter.getFormattedStringToCreationDate(dateString);
    }

}
