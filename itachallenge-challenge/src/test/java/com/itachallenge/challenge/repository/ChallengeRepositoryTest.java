package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.fail;

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ChallengeRepositoryTest {


    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("container url: {}" + container.getReplicaSetUrl("challenges"));
        System.out.println("container host/port: {}/{}" + container.getHost() + " - " + container.getFirstMappedPort());

        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("challenges"));
    }

    @Autowired
    private ChallengeRepository challengeRepository;

    UUID uuid_1 = UUID.fromString("8ecbfe54-fec8-11ed-be56-0242ac120002");
    UUID uuid_2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");


    @BeforeEach
    public void setUp() {

        challengeRepository.deleteAll().block();

        Set<UUID> UUIDSet = new HashSet<>(Arrays.asList(uuid_2, uuid_1));
        Set<UUID> UUIDSet2 = new HashSet<>(Arrays.asList(uuid_2, uuid_1));

        ExampleDocument example = new ExampleDocument(uuid_1, "Example Text 1");
        ExampleDocument example2 = new ExampleDocument(uuid_2, "Example Text 2");
        List<ExampleDocument> exampleList = new ArrayList<ExampleDocument>(Arrays.asList(example2, example));

        UUID uuidLang1 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID uuidLang2 = UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef");
        UUID[] idsLanguages = new UUID[]{uuidLang1, uuidLang2};
        String[] languageNames = new String[]{"name1", "name2"};
        LanguageDocument language1 = getLanguageMocked(idsLanguages[0], languageNames[0]);
        LanguageDocument language2 = getLanguageMocked(idsLanguages[1], languageNames[1]);
        Set<LanguageDocument> languageSet = Set.of(language1, language2);

        List<UUID> solutionList = List.of(UUID.randomUUID(),UUID.randomUUID());

        DetailDocument detail = new DetailDocument("Description", exampleList, "Detail note");

        ChallengeDocument challenge = new ChallengeDocument
                (uuid_1, "Loops", "Level 1", LocalDateTime.now(), detail, languageSet, solutionList, UUIDSet, UUIDSet2);
        ChallengeDocument challenge2 = new ChallengeDocument
                (uuid_2, "If", "Level 2", LocalDateTime.now(), detail, languageSet, solutionList, UUIDSet, UUIDSet2);


        challengeRepository.saveAll(Flux.just(challenge, challenge2)).blockLast();
    }

    private LanguageDocument getLanguageMocked(UUID idLanguage, String languageName){
        LanguageDocument languageIMocked = Mockito.mock(LanguageDocument.class);
        when(languageIMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageIMocked.getLanguageName()).thenReturn(languageName);
        return languageIMocked;
    }


    @DisplayName("Repository not null Test")
    @Test
    void testDB() {

        assertNotNull(challengeRepository);

    }

    @DisplayName("Find All Test")
    @Test
    void findAllTest() {

        Flux<ChallengeDocument> challenges = challengeRepository.findAll();

        StepVerifier.create(challenges)
                .expectNextCount(2)
                .verifyComplete();
    }

    @DisplayName("Find Challenges for a Page")
    @Test
    void findAllByPageTest() {

        Pageable pageNumber0Size1 = PageRequest.of(0,1);
        Pageable pageNumber0Size2 = PageRequest.of(0,2);
        Pageable pageNumber1Size1 = PageRequest.of(1,1);
        Pageable pageNumber1Size2 = PageRequest.of(1,2);

        Flux<ChallengeDocument> challengesPageNumber0Size1 = challengeRepository.findAllBy(pageNumber0Size1);
        StepVerifier.create(challengesPageNumber0Size1)
                .expectNextCount(1)
                .verifyComplete();

        Flux<ChallengeDocument> challengesPageNumber0Size2 = challengeRepository.findAllBy(pageNumber0Size2);
        StepVerifier.create(challengesPageNumber0Size2)
                .expectNextCount(2)
                .verifyComplete();

        Flux<ChallengeDocument> challengesPageNumber1Size1 = challengeRepository.findAllBy(pageNumber1Size1);
        StepVerifier.create(challengesPageNumber1Size1)
                .expectNextCount(1)
                .verifyComplete();

        Flux<ChallengeDocument> challengesPageNumber1Size2 = challengeRepository.findAllBy(pageNumber1Size2);
        StepVerifier.create(challengesPageNumber1Size2)
                .expectNextCount(0)
                .verifyComplete();

    }

    @DisplayName("Exists by UUID Test")
    @Test
    void existsByUuidTest() {
        Boolean exists = challengeRepository.existsByUuid(uuid_1).block();
        assertEquals(true, exists);
    }

    @DisplayName("Find by UUID Test")
    @Test
    void findByUuidTest() {

        Mono<ChallengeDocument> firstChallenge = challengeRepository.findByUuid(uuid_1);
        firstChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getUuid(), uuid_1),
                () -> fail("Challenge not found: " + uuid_1));

        Mono<ChallengeDocument> secondChallenge = challengeRepository.findByUuid(uuid_2);
        secondChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getUuid(), uuid_2),
                () -> fail("Challenge not found: " + uuid_2));
    }

    @DisplayName("Delete by UUID Test")
    @Test
    void deleteByUuidTest() {

        Mono<ChallengeDocument> firstChallenge = challengeRepository.findByUuid(uuid_1);
        firstChallenge.blockOptional().ifPresentOrElse(
                u -> {
                    Mono<Void> deletion = challengeRepository.deleteByUuid(uuid_1);
                    StepVerifier.create(deletion)
                            .expectComplete()
                            .verify();
                },
                () -> fail("Challenge to delete not found: " + uuid_1)
        );

        Mono<ChallengeDocument> secondChallenge = challengeRepository.findByUuid(uuid_2);
        secondChallenge.blockOptional().ifPresentOrElse(
                u -> {
                    Mono<Void> deletion = challengeRepository.deleteByUuid(uuid_2);
                    StepVerifier.create(deletion)
                            .expectComplete()
                            .verify();
                },
                () -> fail("Challenge to delete not found: " + uuid_2)
        );
    }

    @DisplayName("Find by Level Test")
    @Test
    void findByLevelTest() {

        Mono<ChallengeDocument> firstChallenge = challengeRepository.findByLevel("Level 1");
        firstChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getLevel(), "Level 1"),
                () -> fail("Challenge not found: " + uuid_1));

        Mono<ChallengeDocument> secondChallenge = challengeRepository.findByLevel("Level 2");
        secondChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getLevel(), "Level 2"),
                () -> fail("Challenge not found: " + uuid_2));
    }

    @DisplayName("Find by Title Test")
    @Test
    void findByChallengeTitleTest() {

        Mono<ChallengeDocument> firstChallenge = challengeRepository.findByTitle("Loops");
        firstChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getTitle(), "Loops"),
                () -> fail("Challenge with name Loops  not found."));

        Mono<ChallengeDocument> secondChallenge = challengeRepository.findByTitle("If");
        secondChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getTitle(), "If"),
                () -> fail("Challenge with name If not found."));
    }


}