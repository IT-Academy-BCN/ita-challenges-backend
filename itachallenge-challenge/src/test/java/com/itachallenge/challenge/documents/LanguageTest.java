package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class LanguageTest {

    @Test
    public void testLanguageClass() {
        int idLanguage = 1;
        String languageName = "Java";
        Set<UUID> idChallenges = new HashSet<>();
        idChallenges.add(UUID.randomUUID());
        idChallenges.add(UUID.randomUUID());

        Language language = new Language(idLanguage, languageName, idChallenges);

        assertEquals(idLanguage, language.getIdLanguage());
        assertEquals(languageName, language.getLanguageName());
        assertEquals(idChallenges, language.getIdChallenges());

        int newIdLanguage = 2;
        String newLanguageName = "Python";
        Set<UUID> newIdChallenges = new HashSet<>();
        newIdChallenges.add(UUID.randomUUID());
        newIdChallenges.add(UUID.randomUUID());

        language.setIdLanguage(newIdLanguage);
        language.setLanguageName(newLanguageName);
        language.setIdChallenges(newIdChallenges);

        assertEquals(newIdLanguage, language.getIdLanguage());
        assertEquals(newLanguageName, language.getLanguageName());
        assertEquals(newIdChallenges, language.getIdChallenges());
    }

    @Test
    public void testLanguageMethods() {
        // Crea una instancia de Language
        Language language = Language.builder()
                .idLanguage(1)
                .languageName("Java")
                .idChallenges(new HashSet<>())
                .build();

        // Prueba los métodos getter y setter
        int newIdLanguage = 2;
        language.setIdLanguage(newIdLanguage);
        assertEquals(newIdLanguage, language.getIdLanguage());

        String newLanguageName = "Python";
        language.setLanguageName(newLanguageName);
        assertEquals(newLanguageName, language.getLanguageName());

        Set<UUID> newIdChallenges = new HashSet<>();
        language.setIdChallenges(newIdChallenges);
        assertEquals(newIdChallenges, language.getIdChallenges());

        // Prueba el método equals()
        Language equalLanguage = Language.builder()
                .idLanguage(newIdLanguage)
                .languageName(newLanguageName)
                .idChallenges(newIdChallenges)
                .build();
        assertEquals(language, equalLanguage);

        // Prueba el método hashCode()
        assertEquals(language.hashCode(), equalLanguage.hashCode());
    }
}
