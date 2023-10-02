package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.exception.ConverterException;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class GenericDocumentToDtoConverter <S,D> {

    public Flux<D> convertDocumentFluxToDtoFlux(Flux<S> documentFlux, Class<D> dtoClass) {
        return documentFlux.map(doc -> convertDocumentToDto(doc, dtoClass));
    }

    static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public D convertDocumentToDto(S document, Class<D> dtoClass) throws ConverterException {
        ModelMapper mapper = new ModelMapper();
        Converter<LocalDateTime, String> converterFromLocalDateTimeToString = new AbstractConverter<>() {
            @Override
            protected String convert(LocalDateTime creationDateFromDocument) {
                return creationDateFromDocument.format(CUSTOM_FORMATTER);
            }
        };

        /**
         * If you want to map the different field's name document/dto
         * mapper.createTypeMap(ChallengeDocument.class, ChallengeDto.class)
         *                 .addMapping(ChallengeDocument::getUuid, ChallengeDto::setChallengeId)
         *                 .addMapping(ChallengeDocument::getTitle, ChallengeDto::setChallengeTitle);
         */
        mapper.createTypeMap(document, dtoClass);
        mapper.addConverter(converterFromLocalDateTimeToString);
        return mapper.map(document, dtoClass);
    }




}
