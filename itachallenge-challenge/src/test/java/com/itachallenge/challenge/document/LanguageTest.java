package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageTest {

    @Test
    void getIdLanguage() {
        UUID uuid = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        LanguageDocument language = new LanguageDocument(uuid, null);
        assertEquals(uuid, language.getIdLanguage());
    }

    @Test
    void getLanguageName() {
        String languageName = "Javascript";
        LanguageDocument language = new LanguageDocument(null, languageName);
        assertEquals(languageName, language.getLanguageName());
    }
}
