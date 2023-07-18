package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SampleDtoConverter extends ConverterAbstract<ChallengeDocument, ChallengeDto>{

    //setting fromClass to Class
    public SampleDtoConverter(){
        super(ChallengeDocument.class, ChallengeDto.class);
    }

    public Flux<ChallengeDto> convertToDto(Flux<ChallengeDocument> documentFlux) {
        return documentFlux.map(this::convert);
    }

    @Override
    protected ChallengeDto convert(ChallengeDocument object) throws ConverterException {
        ChallengeDto dto = super.convert(object);
        dto.setCreationDate(getFormattedCreationDateTime(object.getCreationDate()));
        return dto;
    }

    //method to set document creationDate attribute into requested UTC String .json format
    private String getFormattedCreationDateTime(LocalDateTime creationDateDocument) {

        ZoneId zoneId = ZoneId.of("Europe/Paris");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(creationDateDocument, zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return zonedDateTime.format(formatter);

    }

}
