package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.springframework.stereotype.Component;

@Component
public class SampleDocConverter extends ConverterAbstract<ChallengeDto, ChallengeDocument> {

    public SampleDocConverter() {
        super(ChallengeDto.class, ChallengeDocument.class);
    }

    @Override
    public ChallengeDocument convert(ChallengeDto object) throws ConverterException {
        ConverterAbstract<ChallengeDto, ChallengeDocument> output = new ConverterAbstract<>(ChallengeDto.class, ChallengeDocument.class);
        return output.convert(object);
    }

    /*@Override
    public ChallengeDto convertToDto(ChallengeDocument object) throws ConverterException {
        ConverterAbstract<ChallengeDocument, ChallengeDto> output = new ConverterAbstract<>(ChallengeDocument.class, ChallengeDto.class);
        return output.convertToDto(object);
    }*/
}