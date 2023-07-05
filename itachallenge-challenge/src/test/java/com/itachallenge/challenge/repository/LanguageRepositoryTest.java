package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.util.AssertionErrors.fail;

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class LanguageRepositoryTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("container url: {}" + container.getReplicaSetUrl("languages"));
        System.out.println("container host/port: {}/{}" + container.getHost() + " - " + container.getFirstMappedPort());

        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("languages"));
    }

    @Autowired
    private LanguageRepository languageRepository;

    UUID uuid_1 = UUID.fromString("8ecbfe54-fec8-11ed-be56-0242ac120002");
    UUID uuid_2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");

    @BeforeEach
    public void setUp() {

        languageRepository.deleteAll().block();

        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
        UUID idLanguage2 = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80297");
        Set<UUID> UUIDSet = new HashSet<>(Arrays.asList(uuid_2, uuid_1));
        Set<UUID> UUIDSet2 = new HashSet<>(Arrays.asList(uuid_2, uuid_1));

        LanguageDocument language = new LanguageDocument(idLanguage, "Java", UUIDSet);
        LanguageDocument language2 = new LanguageDocument(idLanguage2, "Python", UUIDSet2);
        Set<LanguageDocument> languageSet = new HashSet<>(Arrays.asList(language2, language));

        languageRepository.saveAll(Flux.just(language, language2)).blockLast();
    }

    @DisplayName("Repository not null Test")
    @Test
    void testDB() {

        assertNotNull(languageRepository);

    }

    @DisplayName("Find All Test")
    @Test
    void findAllTest() {

        Flux<LanguageDocument> languages = languageRepository.findAll();

        StepVerifier.create(languages)
                .expectNextCount(2)
                .verifyComplete();
    }

    @DisplayName("Exists by Id Test")
    @Test
    void existsByIdTest() {
        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
        Boolean exists = languageRepository.existsById(idLanguage).block();
        assertEquals(exists, true);
    }

    @DisplayName("Find by Id Test")
    @Test
    void findByIdTest() {

        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
        UUID idLanguage2 = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80297");
        Mono<LanguageDocument> firstLanguage = languageRepository.findByIdLanguage(idLanguage);
        firstLanguage.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getIdLanguage(), 1),
                () -> fail("Language with id " + idLanguage + " not found"));

        Mono<LanguageDocument> secondLanguage = languageRepository.findByIdLanguage(idLanguage2);
        secondLanguage.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getIdLanguage(), 2),
                () -> fail("Language with id " + idLanguage2 + " not found"));
    }

    @DisplayName("Delete by Id Test")
    @Test
    void deleteByIdTest() {

        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
        UUID idLanguage2 = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80297");
        Mono<LanguageDocument> firstLanguage = languageRepository.findByIdLanguage(idLanguage);
        firstLanguage.blockOptional().ifPresentOrElse(
                u -> {
                    Mono<Void> deletion = languageRepository.deleteByIdLanguage(idLanguage);
                    StepVerifier.create(deletion)
                            .expectComplete()
                            .verify();
                },
                () -> fail("Language with id " + idLanguage + " not found")
        );

        Mono<LanguageDocument> secondLanguage = languageRepository.findByIdLanguage(idLanguage2);
        secondLanguage.blockOptional().ifPresentOrElse(
                u -> {
                    Mono<Void> deletion = languageRepository.deleteByIdLanguage(idLanguage2);
                    StepVerifier.create(deletion)
                            .expectComplete()
                            .verify();
                },
                () -> fail("Language with id " + idLanguage2 + " not found")
        );
    }

}
