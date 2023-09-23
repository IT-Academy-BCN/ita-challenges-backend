package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ChallengeDocumentToDtoConverter {

    static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ChallengeDto convertDocumentToDto(ChallengeDocument document) throws ConverterException {

        ModelMapper mapper = new ModelMapper();
        Converter<LocalDateTime, String> fromLocalDateTimeToString = new AbstractConverter<>() {
            @Override
            protected String convert(LocalDateTime creationDateFromDocument) {
                return creationDateFromDocument.format(CUSTOM_FORMATTER);
            }
        };

        mapper.createTypeMap(ChallengeDocument.class, ChallengeDto.class);
        mapper.addConverter(fromLocalDateTimeToString);
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        return mapper.map(document, ChallengeDto.class);
    }

    public Flux<ChallengeDto> convertDocumentFluxToDtoFlux(Flux<ChallengeDocument> documentFlux) {
        return documentFlux.map(this::convertDocumentToDto);
    }
}
