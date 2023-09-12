package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
public class ChallengeConverterDto extends ConverterAbstract<ChallengeDocument, ChallengeDto>{

    //setting fromClass to Class
    public ChallengeConverterDto(){
        super(ChallengeDocument.class, ChallengeDto.class);
    }

    public Flux<ChallengeDto> convertToDto(Flux<ChallengeDocument> documentFlux) {
        return documentFlux.map(this::convert);
    }

    @Override
    protected ChallengeDto convert(ChallengeDocument document) throws ConverterException {
        ChallengeDto dto = super.convert(document);

        dto.setLanguages(document.getLanguages().stream()
                .map(this::convertLanguageDocumentToDto).collect(Collectors.toSet()));

        dto.setCreationDate(getFormattedCreationDateTime(document.getCreationDate()));
        return dto;
    }

    private LanguageDto convertLanguageDocumentToDto(LanguageDocument languageDocument) {
        return new LanguageDto(languageDocument.getIdLanguage(), languageDocument.getLanguageName());
    }

    //method to set document creationDate attribute into requested UTC String .json format
    private String getFormattedCreationDateTime(LocalDateTime creationDateDocument) {

        ZoneId zoneId = ZoneId.of("Europe/Paris");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(creationDateDocument, zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return zonedDateTime.format(formatter);

    }

}
