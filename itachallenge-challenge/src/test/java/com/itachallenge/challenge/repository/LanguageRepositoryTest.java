package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.integration.AbstractMongoDataTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class LanguageRepositoryTest extends AbstractMongoDataTest {

    @Autowired
    private LanguageRepository languageRepository;

    UUID uuidLang1;
    UUID uuidLang2;

    @BeforeEach
    void setUp() {
        uuidLang1 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        uuidLang2 = UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef");

        languageRepository.deleteAll().block();

        LanguageDocument language1 =
                new LanguageDocument(uuidLang1, "Java", "https://image-default.com/java.png");

        LanguageDocument language2 =
                new LanguageDocument(uuidLang2, "Python", "https://image-default.com/python.png");

        languageRepository.saveAll(Flux.just(language1, language2)).blockLast();
    }

    @Test
    void testDB() {
        assertNotNull(languageRepository);
    }

    @Test
    void findAllTest() {
        StepVerifier.create(languageRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void existsByIdTest() {
        Boolean exists = languageRepository.existsById(uuidLang1).block();
        assertEquals(true, exists);
    }

    @Test
    void findByIdTest() {
        StepVerifier.create(languageRepository.findByIdLanguage(uuidLang1))
                .assertNext(l -> assertEquals(uuidLang1, l.getIdLanguage()))
                .verifyComplete();

        StepVerifier.create(languageRepository.findByIdLanguage(uuidLang2))
                .assertNext(l -> assertEquals(uuidLang2, l.getIdLanguage()))
                .verifyComplete();
    }

    @Test
    void deleteByIdTest() {
        StepVerifier.create(languageRepository.deleteByIdLanguage(uuidLang1)).verifyComplete();
        StepVerifier.create(languageRepository.deleteByIdLanguage(uuidLang2)).verifyComplete();

        StepVerifier.create(languageRepository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findFirstByLanguageName_test() {
        LanguageDocument language = languageRepository.findFirstByLanguageName("Java").block();
        assertNotNull(language);
        assertEquals(uuidLang1, language.getIdLanguage());
    }
}