package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class LanguageTest {

    @Test
    void testLanguageClass() {
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
    void testGetIdLanguage() {
        // Arrange
        int idLanguage = 1;
        Language language = new Language();
        language.setIdLanguage(idLanguage);

        // Act
        int result = language.getIdLanguage();

        // Assert
        assertEquals(idLanguage, result);
    }

    @Test
    void testSetIdLanguage() {
        // Arrange
        int idLanguage = 1;
        Language language = new Language();

        // Act
        language.setIdLanguage(idLanguage);

        // Assert
        assertEquals(idLanguage, language.getIdLanguage());
    }

    @Test
    void testGetLanguageName() {
        // Arrange
        String languageName = "Test Language";
        Language language = new Language();
        language.setLanguageName(languageName);

        // Act
        String result = language.getLanguageName();

        // Assert
        assertEquals(languageName, result);
    }

    @Test
    void testSetLanguageName() {
        // Arrange
        String languageName = "Test Language";
        Language language = new Language();

        // Act
        language.setLanguageName(languageName);

        // Assert
        assertEquals(languageName, language.getLanguageName());
    }

    @Test
    void testGetIdChallenges() {
        // Arrange
        Set<UUID> idChallenges = new HashSet<>();
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();
        idChallenges.add(challengeId1);
        idChallenges.add(challengeId2);

        Language language = new Language();
        language.setIdChallenges(idChallenges);

        // Act
        Set<UUID> result = language.getIdChallenges();

        // Assert
        assertEquals(idChallenges, result);
    }

    @Test
    void testSetIdChallenges() {
        // Arrange
        Set<UUID> idChallenges = new HashSet<>();
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();
        idChallenges.add(challengeId1);
        idChallenges.add(challengeId2);

        Language language = new Language();

        // Act
        language.setIdChallenges(idChallenges);

        // Assert
        assertEquals(idChallenges, language.getIdChallenges());
    }
}
