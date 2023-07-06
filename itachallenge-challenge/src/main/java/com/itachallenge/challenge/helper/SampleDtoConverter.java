package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.exception.ConverterException;

public class SampleDtoConverter extends ConverterAbstract<ChallengeDocument, ChallengeDto>{

    public SampleDtoConverter(){
        super(ChallengeDocument.class, ChallengeDto.class);
    }

    @Override
    public ChallengeDto convert(ChallengeDocument object) throws ConverterException {
        ConverterAbstract<ChallengeDocument, ChallengeDto> output = new ConverterAbstract<>(ChallengeDocument.class, ChallengeDto.class);
        return output.convert(object);
    }

}
