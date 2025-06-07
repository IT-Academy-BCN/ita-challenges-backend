package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.document.TagDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.util.AssertionErrors.fail;

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TagRepositoryTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("container url: {}" + container.getReplicaSetUrl("languages"));
        System.out.println("container host/port: {}/{}" + container.getHost() + " - " + container.getFirstMappedPort());

        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("tags"));
    }

    @Autowired
    private TagRepository tagRepository;

    UUID uuid_1 = UUID.fromString("8ecbfe54-fec8-11ed-be56-0242ac120002");
    UUID uuid_2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");

    UUID uuidLang1, uuidLang2;

    @BeforeEach
    public void setUp() {

        uuidLang1 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        uuidLang2 = UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef");

        UUID uuidTag1 = UUID.randomUUID();
        UUID uuidTag2 = UUID.randomUUID();

        tagRepository.deleteAll().block();

        TagDocument tag1 = new TagDocument(uuidTag1, "POO", "Programació orientada a objectes", uuidLang1);
        TagDocument tag2 = new TagDocument(uuidTag2, "Bucles", "Bucles 'for' y 'while'", uuidLang2);

        Set<TagDocument> tagSet = new HashSet<>(Arrays.asList(tag1, tag2));

        tagRepository.saveAll(Flux.just(tag1, tag2)).blockLast();
    }

    @DisplayName("Repository not null Test")
    @Test
    void testDB() {

        assertNotNull(tagRepository);

    }

    @DisplayName("Find All Test")
    @Test
    void findAllTagsTest() {

        Flux<TagDocument> tags = tagRepository.findAll();

        StepVerifier.create(tags)
                .expectNextCount(2)
                .verifyComplete();
    }

    @DisplayName("Find by TagName")
    @Test
    void findByNameTagTest() {

        String tagNameByFound = "POO";

        Mono<TagDocument> tag1 = tagRepository.findByTagName(tagNameByFound);
        tag1.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getTagName(), tagNameByFound),
                () -> fail("Tag with name " + tagNameByFound + " not found"));

        String tagNameByFound2 = "Bucles";

        Mono<TagDocument> tag2 = tagRepository.findByTagName(tagNameByFound2);
        tag2.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getTagName(), tagNameByFound2),
                () -> fail("Tag with name " + tagNameByFound2 + " not found"));
    }

    @DisplayName("Find by Language ID")
    @Test
    void findByIdLanguageTest() {
        Flux<TagDocument> tagsByLanguage = tagRepository.findByIdLanguage(uuidLang1);

        StepVerifier.create(tagsByLanguage)
                .expectNextMatches(tag -> tag.getTagName().equals("POO") && tag.getLanguageId().equals(uuidLang1))
                .verifyComplete();
    }


}
