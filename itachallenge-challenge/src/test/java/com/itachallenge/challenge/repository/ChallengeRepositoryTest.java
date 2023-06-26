package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.document.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
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

        Example example = new Example(uuid_1, "Example Text 1");
        Example example2 = new Example(uuid_2, "Example Text 2");
        List<Example> exampleList = new ArrayList<Example>(Arrays.asList(example2, example));

        int[] idsLanguages = new int[]{1, 2};
        String[] languageNames = new String[]{"name1", "name2"};
        Language language1 = getLanguageMocked(idsLanguages[0], languageNames[0]);
        Language language2 = getLanguageMocked(idsLanguages[1], languageNames[1]);
        Set<Language> languageSet = Set.of(language1, language2);

        Solution solution = new Solution(uuid_1, "Solution Text 1", 1);
        Solution solution2 = new Solution(uuid_2, "Solution Text 2", 2);
        List<Solution> solutionList = new ArrayList<>(Arrays.asList(solution, solution2));

        Detail detail = new Detail("Description", exampleList, "Detail note");

        Challenge challenge = new Challenge
                (uuid_1, "Level 1", "Loops", languageSet, LocalDateTime.now(), detail, solutionList, UUIDSet, UUIDSet2);
        Challenge challenge2 = new Challenge
                (uuid_2, "Level 2", "If", languageSet, LocalDateTime.now(), detail, solutionList, UUIDSet, UUIDSet2);


        challengeRepository.saveAll(Flux.just(challenge, challenge2)).blockLast();
    }

    private Language getLanguageMocked(int idLanguage, String languageName){
        Language languageIMocked = Mockito.mock(Language.class);
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

        Flux<Challenge> challenges = challengeRepository.findAll();

        StepVerifier.create(challenges)
                .expectNextCount(2)
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

        Mono<Challenge> firstChallenge = challengeRepository.findByUuid(uuid_1);
        firstChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getUuid(), uuid_1),
                () -> fail("Challenge not found: " + uuid_1));

        Mono<Challenge> secondChallenge = challengeRepository.findByUuid(uuid_2);
        secondChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getUuid(), uuid_2),
                () -> fail("Challenge not found: " + uuid_2));
    }

    @DisplayName("Delete by UUID Test")
    @Test
    void deleteByUuidTest() {

        Mono<Challenge> firstChallenge = challengeRepository.findByUuid(uuid_1);
        firstChallenge.blockOptional().ifPresentOrElse(
                u -> {
                    Mono<Void> deletion = challengeRepository.deleteByUuid(uuid_1);
                    StepVerifier.create(deletion)
                            .expectComplete()
                            .verify();
                },
                () -> fail("Challenge to delete not found: " + uuid_1)
        );

        Mono<Challenge> secondChallenge = challengeRepository.findByUuid(uuid_2);
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

        Mono<Challenge> firstChallenge = challengeRepository.findByLevel("Level 1");
        firstChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getLevel(), "Level 1"),
                () -> fail("Challenge not found: " + uuid_1));

        Mono<Challenge> secondChallenge = challengeRepository.findByLevel("Level 2");
        secondChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getLevel(), "Level 2"),
                () -> fail("Challenge not found: " + uuid_2));
    }

    @DisplayName("Find by Title Test")
    @Test
    void findByChallengeTitleTest() {

        Mono<Challenge> firstChallenge = challengeRepository.findByTitle("Loops");
        firstChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getTitle(), "Loops"),
                () -> fail("Challenge with name Loops  not found."));

        Mono<Challenge> secondChallenge = challengeRepository.findByTitle("If");
        secondChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getTitle(), "If"),
                () -> fail("Challenge with name If not found."));
    }


}
