package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageTest {

    @Test
    void getIdLanguage() {
        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
        LanguageDocument language = new LanguageDocument(idLanguage, null, null);

        assertEquals(idLanguage, language.getIdLanguage());
    }

    @Test
    void getLanguageName() {
        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80297");
        String languageName = "Java";
        LanguageDocument language = new LanguageDocument(idLanguage, languageName, null);

        assertEquals(languageName, language.getLanguageName());
    }

    @Test
    void getIdChallenges() {
        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80272");
        Set<UUID> idChallenges = new HashSet<>();
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();
        idChallenges.add(challengeId1);
        idChallenges.add(challengeId2);
        LanguageDocument language = new LanguageDocument(idLanguage, null, idChallenges);

        assertEquals(idChallenges, language.getIdChallenges());
    }
}
