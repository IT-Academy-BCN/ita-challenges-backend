package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

public class SampleDocConverterTest {

    private SampleDocConverter converter = new SampleDocConverter();

    private ChallengeDocument challengeDoc1;

    private ChallengeDocument challengeDoc2;

    private ChallengeDto challengeDto;

    private LanguageDocument languageDocument;

    private LanguageDocument languageDocument2;

    @BeforeEach
    public void setup() {
        //converter = new SampleDocConverter();
        UUID challengeRandomId = UUID.randomUUID();
        String title = "Random title";
        String level = "some level";
        LocalDateTime creationDate = LocalDateTime.of(2023, 6, 5, 12, 30, 0);
        int[] idsLanguages = new int[]{1, 2};
        String[] languageNames = new String[]{"name1", "name2"};

        languageDocument = getLanguageMocked(idsLanguages[0], languageNames[0]);
        languageDocument2 = getLanguageMocked(idsLanguages[1], languageNames[1]);

        challengeDoc1 = getChallengeMocked(challengeRandomId, title, level,
                Set.of(languageDocument, languageDocument2), creationDate);
        challengeDoc2 = getChallengeMocked(challengeRandomId, "another language", "other level",
                Set.of(languageDocument, languageDocument2), creationDate);

        challengeDto = getChallengeDtoMocked(challengeRandomId, title, level,
                Set.of(getLanguageDtoMocked(idsLanguages[0], languageNames[0])), creationDate.toString());
    }

   /* @Test
    public void testConvertToDto() throws ConverterException {

        ChallengeDocument challengeOrigin = challengeDoc1;
        ChallengeDto challengeDto = converter.convertToDto(challengeOrigin);

        assertNotNull(challengeDto);
        assertThat(challengeOrigin).usingRecursiveComparison().isEqualTo(challengeDto);

    }*/

    /*@Test
    public void testConvertToDoc() throws ConverterException {

        ChallengeDto challengeOrigin = challengeDto;
        ChallengeDocument challengeDoc = converter.convertToDoc(challengeOrigin);

        assertNotNull(challengeDoc);
        Assertions.assertEquals(challengeOrigin, challengeDoc);
        //assertThat(challengeOrigin).usingRecursiveComparison().isEqualTo(challengeDoc);
    }*/

    /*@Test
    public void testConvertToDoc() throws ConverterException {
        ChallengeDocument expectedDoc = challengeDoc1;
        //ChallengeDto expectedDto = Mockito.mock(ChallengeDto.class);
        ChallengeDocument resultDoc = converter.convertToDoc(challengeDto);

        assertNotNull(resultDoc);
        assertThat(resultDoc).isEqualTo(expectedDoc);
    }*/

    @Test
    public void testConvertToDoc() throws ConverterException {
        ChallengeDto expectedDto = Mockito.mock(ChallengeDto.class);
        ChallengeDocument challengeDoc = converter.convert(expectedDto);

        assertNotNull(challengeDoc);
        assertEquals(expectedDto.getUuid(), challengeDoc.getUuid());
        assertEquals(expectedDto.getTitle(), challengeDoc.getTitle());
        assertEquals(expectedDto.getLevel(), challengeDoc.getLevel());
        //assertEquals(expectedDto.getLanguages(), challengeDoc.getLanguages());
        //assertEquals(expectedDto.getCreationDate(), challengeDoc.getCreationDate().toString());
        // Compara los campos languages y creationDate según corresponda

        // Resto de las afirmaciones necesarias
    }


    private ChallengeDocument getChallengeMocked(UUID challengeId, String title, String level,
                                                 Set<LanguageDocument> languageIS, LocalDateTime creationDate){
        ChallengeDocument challengeIMocked = Mockito.mock(ChallengeDocument.class);
        when(challengeIMocked.getUuid()).thenReturn(challengeId);
        when(challengeIMocked.getTitle()).thenReturn(title);
        when(challengeIMocked.getLevel()).thenReturn(level);
        when(challengeIMocked.getLanguages()).thenReturn(languageIS);
        when(challengeIMocked.getCreationDate()).thenReturn(creationDate);
        return challengeIMocked;
    }

    private ChallengeDto getChallengeDtoMocked(UUID challengeId, String title, String level,
                                               Set<LanguageDto> languageIS, String creationDate){
        ChallengeDto challengeDocMocked = Mockito.mock(ChallengeDto.class);
        when(challengeDocMocked.getUuid()).thenReturn(challengeId);
        when(challengeDocMocked.getTitle()).thenReturn(title);
        when(challengeDocMocked.getLevel()).thenReturn(level);
        when(challengeDocMocked.getLanguages()).thenReturn(languageIS);
        when(challengeDocMocked.getCreationDate()).thenReturn(creationDate);
        return challengeDocMocked;
    }

    private LanguageDocument getLanguageMocked(int idLanguage, String languageName){
        LanguageDocument languageIMocked = Mockito.mock(LanguageDocument.class);
        when(languageIMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageIMocked.getLanguageName()).thenReturn(languageName);
        return languageIMocked;
    }

    private LanguageDto getLanguageDtoMocked(int idLanguage, String languageName){
        LanguageDto languageIMocked = Mockito.mock(LanguageDto.class);
        when(languageIMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageIMocked.getLanguageName()).thenReturn(languageName);
        return languageIMocked;
    }

}
