package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class LanguageConverterDto extends ConverterAbstract<LanguageDocument, LanguageDto>{

    //setting fromClass to Class
    public LanguageConverterDto(){
        super(LanguageDocument.class, LanguageDto.class);
    }

    public Flux<LanguageDto> converToDto(Flux<LanguageDocument> documentFlux) {
        return documentFlux.map(this::convert);
    }

    @Override
    protected LanguageDto convert(LanguageDocument document) throws ConverterException {
        return super.convert(document);
    }

}
