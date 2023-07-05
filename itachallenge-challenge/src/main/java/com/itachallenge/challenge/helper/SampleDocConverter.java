package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.springframework.stereotype.Component;

@Component
public class SampleDocConverter extends ConverterAbstract<ChallengeDto, ChallengeDocument> {

    private ConverterAbstract<ChallengeDto, ChallengeDocument> nestedConverter = new ConverterAbstract<>(ChallengeDto.class,
            ChallengeDocument.class);

    public SampleDocConverter() {
        super(ChallengeDto.class, ChallengeDocument.class);
    }

    @Override
    public ChallengeDocument convertToDoc(ChallengeDto object) throws ConverterException {
        ChallengeDocument instance = super.convertToDoc(object);
        /*if (object.nested != null) {
            instance.setNested(nestedConverter.convert(object.nested));
        }*/

        return instance;
    }

    @Override
    public ChallengeDto convertToDto(ChallengeDocument object) throws ConverterException {
        ChallengeDto instance = super.convertToDto(object);
        /*if (object.getNested() != null) {
            instance.nested = nestedConverter.convertBack(object.getNested());
        }*/
        return instance;
    }
}