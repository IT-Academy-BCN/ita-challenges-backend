package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.Locale;
import com.itachallenge.challenge.document.SolutionDocument;
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
import java.util.UUID;

import static com.itachallenge.challenge.document.Locale.EN;
import static com.itachallenge.challenge.document.Locale.ES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.util.AssertionErrors.fail;

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class SolutionRepositoryTest {


    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("container url: {}" + container.getReplicaSetUrl("solutions"));
        System.out.println("container host/port: {}/{}" + container.getHost() + " - " + container.getFirstMappedPort());

        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("solutions"));
    }

    @Autowired
    private SolutionRepository solutionRepository;

    UUID uuid_1 = UUID.fromString("8ecbfe54-fec8-11ed-be56-0242ac120002");
    UUID uuid_2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");

    @BeforeEach
    void setUp(){

        UUID uuidLang1 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID uuidLang2 = UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef");

        solutionRepository.deleteAll().block();

        SolutionDocument solution = new SolutionDocument(uuid_1, "Solution Text 1", uuidLang1, EN);
        SolutionDocument solution2 = new SolutionDocument(uuid_2, "Solution Text 2", uuidLang2, ES);

        solutionRepository.saveAll(Flux.just(solution, solution2)).blockLast();

    }

    @DisplayName("Repository not null Test")
    @Test
    void testDB() {

        assertNotNull(solutionRepository);

    }

    @DisplayName("Find All Test")
    @Test
    void findAllTest() {

        Flux<SolutionDocument> solutions = solutionRepository.findAll();

        StepVerifier.create(solutions)
                .expectNextCount(2)
                .verifyComplete();
    }

    @DisplayName("Exists by UUID and Locale Test")
    @Test
    void existsByUuidTest(Locale locale) {
        Boolean exists = solutionRepository.existsByUuidAndLocale(uuid_1, locale).block();
        assertEquals(true, exists);
    }

    @DisplayName("Find by UUID and Locale Test")
    @Test
    void findByUuidTest(Locale locale) {

        Mono<SolutionDocument> firstSolution = solutionRepository.findByUuidAndLocale(uuid_1, locale);
        firstSolution.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getUuid(), uuid_1),
                () -> fail("Solution not found: " + uuid_1 + " in " + locale));

        Mono<SolutionDocument> secondSolution = solutionRepository.findByUuidAndLocale(uuid_2, locale);
        secondSolution.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getUuid(), uuid_2),
                () -> fail("Solution not found: " + uuid_2 + " in " + locale));
    }

    @DisplayName("Delete by UUID and Locale Test")
    @Test
    void deleteByUuidAndLocaleTest(Locale locale) {

        Mono<SolutionDocument> firstSolution = solutionRepository.findByUuidAndLocale(uuid_1, locale);
        firstSolution.blockOptional().ifPresentOrElse(
                u -> {
                    Mono<Void> deletion = solutionRepository.deleteByUuidAndLocale(uuid_1, locale);
                    StepVerifier.create(deletion)
                            .expectComplete()
                            .verify();
                },
                () -> fail("Solution to delete not found: " + uuid_1 + " in " + locale)
        );

        Mono<SolutionDocument> secondSolution = solutionRepository.findByUuidAndLocale(uuid_2, locale);
        secondSolution.blockOptional().ifPresentOrElse(
                u -> {
                    Mono<Void> deletion = solutionRepository.deleteByUuidAndLocale(uuid_2, locale);
                    StepVerifier.create(deletion)
                            .expectComplete()
                            .verify();
                },
                () -> fail("Solution to delete not found: " + uuid_2 + " in " + locale)
        );
    }

}