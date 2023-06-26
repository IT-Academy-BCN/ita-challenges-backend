package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LanguageTest {

    @Test
    void getIdLanguage() {
        int idLanguage = 1;
        Language language = new Language(idLanguage, null, null);
        assertEquals(idLanguage, language.getIdLanguage());
    }

    @Test
    void getLanguageName() {
        String languageName = "Java";
        Language language = new Language(0, languageName, null);
        assertEquals(languageName, language.getLanguageName());
    }

    @Test
    void getIdChallenges() {
        Set<UUID> idChallenges = new HashSet<>();
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();
        idChallenges.add(challengeId1);
        idChallenges.add(challengeId2);
        Language language = new Language(0, null, idChallenges);
        assertEquals(idChallenges, language.getIdChallenges());
    }
}
