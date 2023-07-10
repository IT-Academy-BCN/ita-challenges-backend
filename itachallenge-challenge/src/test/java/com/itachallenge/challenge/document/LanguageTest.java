package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LanguageTest {

    @Test
    void getIdLanguage() {
        UUID idLanguage = UUID.randomUUID();
        LanguageDocument language = new LanguageDocument(idLanguage, null);
        assertEquals(idLanguage, language.getIdLanguage());
    }

    @Test
    void getLanguageName() {
        String languageName = "Java";
        LanguageDocument language = new LanguageDocument(null, languageName);
        assertEquals(languageName, language.getLanguageName());
    }
}
