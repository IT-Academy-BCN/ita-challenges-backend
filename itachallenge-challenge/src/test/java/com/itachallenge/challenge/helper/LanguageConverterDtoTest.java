package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;

class LanguageConverterDtoTest {

    private final LanguageConverterDto converter = new LanguageConverterDto();

    private LanguageDocument languageDocument1;

    private LanguageDocument languageDocument2;

    private LanguageDto languageDto1;

    private LanguageDto languageDto2;

    @BeforeEach
    public void setUp() {

        UUID[] languageID = new UUID[]{UUID.randomUUID(), UUID.randomUUID()};
        String[] languageNames = new String[]{"Java", "Python"};

        languageDocument1 = new LanguageDocument(languageID[0], languageNames[0]);
        languageDocument2 = new LanguageDocument(languageID[1], languageNames[1]);

        languageDto1 = new LanguageDto(languageID[0], languageNames[0]);
        languageDto2 = new LanguageDto(languageID[1], languageNames[1]);

    }

    @Test
    @DisplayName("Conversion from document to dto. Testing 'convert' method from ConverterAbstract, inherited by the subclass")
    void testConvertToDto() throws ConverterException {
        LanguageDocument languageDocumentMocked = languageDocument1;
        LanguageDto resultDto = converter.convert(languageDocumentMocked);
        LanguageDto expectedDto = languageDto1;
        assertEquals(expectedDto.getIdLanguage(), resultDto.getIdLanguage());
        assertEquals(expectedDto.getLanguageName(), resultDto.getLanguageName());
    }

    @Test
    @DisplayName("Test convertToDto method")
    void testConvertToDtoMethod() {
        Flux<LanguageDocument> documentFlux = Flux.just(languageDocument1, languageDocument2);

        Flux<LanguageDto> resultFlux = converter.convertToDto(documentFlux);

        StepVerifier.create(resultFlux)
                .assertNext(languageDto -> {
                    Assertions.assertEquals(languageDto1.getIdLanguage(), languageDto.getIdLanguage());
                    Assertions.assertEquals(languageDto1.getLanguageName(), languageDto.getLanguageName());
                })
                .assertNext(languageDto -> {
                    Assertions.assertEquals(languageDto2.getIdLanguage(), languageDto.getIdLanguage());
                    Assertions.assertEquals(languageDto2.getLanguageName(), languageDto.getLanguageName());
                })
                .expectComplete()
                .verify();
    }
}