package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class LanguageConverterDto {

    @Autowired
    private ModelMapper modelMapper;

    public Flux<LanguageDto> convertFluxEntityToFluxDto(Flux<LanguageDocument> documentFlux) {
        return documentFlux.map(this::convertEntityToDto);
    }

    public LanguageDto convertEntityToDto(LanguageDocument document) throws ConverterException {
        ModelMapper entityToDto = new ModelMapper();
        return entityToDto.map(document, LanguageDto.class);
    }

}
