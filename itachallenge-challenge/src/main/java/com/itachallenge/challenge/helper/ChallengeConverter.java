package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ChallengeConverter {

    public ChallengeDto convertDocumentToDto(ChallengeDocument document) throws ConverterException {
        ModelMapper entityToDto = new ModelMapper();
        return entityToDto.map(document, ChallengeDto.class);
    }
    public Flux<ChallengeDto> convertDocumentFluxToDtoFlux(Flux<ChallengeDocument> documentFlux) {
        return documentFlux.map(this::convertDocumentToDto);
    }

}
