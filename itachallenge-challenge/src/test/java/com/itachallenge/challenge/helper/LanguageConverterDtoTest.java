package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

public class LanguageConverterDtoTest {

    //private final LanguageConverterDto converter = new LanguageConverterDto();
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
        assertThat(expectedDto).usingRecursiveComparison().isEqualTo(resultDto);
    }

}
