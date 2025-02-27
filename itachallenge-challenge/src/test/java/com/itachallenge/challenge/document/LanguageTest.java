package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageTest {

    @Test
    void getIdLanguage() {
        UUID uuid = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        LanguageDocument language = new LanguageDocument(uuid, null, null);
        assertEquals(uuid, language.getIdLanguage());
    }

    @Test
    void getLanguageName() {
        String languageName = "Javascript";
        LanguageDocument language = new LanguageDocument(null, languageName, null );
        assertEquals(languageName, language.getLanguageName());
    }

    @Test
    void getLanguageImage(){
        String languageImage = "https://res.cloudinary.com/itachallenge/image/upload/v1739361249/language_icon_Javascript_asgn04.svg";
        LanguageDocument language = new LanguageDocument(null, null, languageImage );
        assertEquals(languageImage, language.getLanguageImage());
    }
}
