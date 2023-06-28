package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import lombok.AllArgsConstructor;
import org.bouncycastle.asn1.cmp.Challenge;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.mapping.Language;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@AllArgsConstructor
public class ConverterMapper {

    private final ModelMapper modelMapper;

    public Flux<ChallengeDto> fromChallengeToChallengeDto(Flux<ChallengeDocument> challengeDocumentFlux) {
        return challengeDocumentFlux.map(challenge -> modelMapper.map(challenge, ChallengeDto.class));
    }
}
