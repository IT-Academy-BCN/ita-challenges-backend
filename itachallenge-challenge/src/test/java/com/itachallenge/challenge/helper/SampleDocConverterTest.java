package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class SampleDocConverterTest {

    private SampleDocConverter converter;

    private ChallengeDocument challengeDoc1;

    private ChallengeDocument challengeDoc2;

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
    }

    @Test
    public void testConvertToDto() throws ConverterException {

        ChallengeDocument challengeOrigin = challengeDoc1;
        ChallengeDto challengeDto = converter.convertToDto(challengeOrigin);

        assertNotNull(challengeDto);
        Assertions.assertEquals(challengeDto.getUuid(), challengeOrigin.getUuid());


        // Crear un objeto ChallengeDto para la conversión
        ChallengeDto dto = new ChallengeDto();
        // Establecer los valores de las propiedades en el objeto dto

        // Convertir el objeto dto a ChallengeDocument
        ChallengeDocument document = converter.convertToDoc(dto);

        // Verificar que el objeto document se haya creado correctamente
        assertNotNull(document);
        // Verificar que los valores de las propiedades en dto y document sean iguales
        // (puedes usar los métodos getter de document para obtener los valores y compararlos con los de dto)
    }

    @Test
    public void testConvertToDoc() throws ConverterException {
        // Crear un objeto ChallengeDocument para la conversión inversa
        ChallengeDocument document = new ChallengeDocument();
        // Establecer los valores de las propiedades en el objeto document

        // Convertir el objeto document a ChallengeDto
        ChallengeDto dto = converter.convertToDto(document);

        // Verificar que el objeto dto se haya creado correctamente
        assertNotNull(dto);
        // Verificar que los valores de las propiedades en document y dto sean iguales
        // (puedes usar los métodos getter de dto para obtener los valores y compararlos con los de document)
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

    private LanguageDocument getLanguageMocked(int idLanguage, String languageName){
        LanguageDocument languageIMocked = Mockito.mock(LanguageDocument.class);
        when(languageIMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageIMocked.getLanguageName()).thenReturn(languageName);
        return languageIMocked;
    }

}
