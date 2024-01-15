package com.itachallenge.user.repository;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserSolutionRepositoryTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("container url: {}" + container.getReplicaSetUrl("users"));
        System.out.println("container host/port: {}/{}" + container.getHost() + " - " + container.getFirstMappedPort());

        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("users"));
    }

    @Autowired
    private IUserSolutionRepository userSolutionRepository;

    private static List<UserSolutionDocument> userSolutions;

    private static UUID testUuid;
    private static UUID testUserUuid;
    private static UUID testChallengeUuid;
    private static UUID testLanguageUuid;
    private static int allMedium;
    private static int allBookmarked;
    private static int score;
    private static int all;
    private static UUID testSolutionDocumentUuid;

    @BeforeAll
    public static void deserializeJson() throws IOException {

        JsonNode node;
        userSolutions = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try (InputStream inputStream = TypeReference.class.getResourceAsStream("/user_score.json")) {
            node = objectMapper.readValue(inputStream, JsonNode.class);
            System.out.println(node);
        } catch (IOException e) {
            throw new IOException("Failed to read JSON data", e);
        }

        for(JsonNode field : node){

            testUuid = UUID.fromString(field.get("_id").get("$uuid").asText());
            testUserUuid = UUID.fromString(field.get("user_id").get("$uuid").asText());
            testChallengeUuid = UUID.fromString(field.get("challenge_id").get("$uuid").asText());
            testLanguageUuid = UUID.fromString(field.get("language_id").get("$uuid").asText());
            allBookmarked = field.get("bookmarked").asBoolean() ? allBookmarked + 1 : allBookmarked;
            allMedium = field.get("status").asText().equals("medium") ? allMedium + 1 : allMedium;
            score = field.get("score").asInt();
            all++;


            UserSolutionDocument userSolution = UserSolutionDocument.builder()
                    .uuid(UUID.fromString(field.get("_id").get("$uuid").asText()))
                    .userId(UUID.fromString(field.get("user_id").get("$uuid").asText()))
                    .challengeId(UUID.fromString(field.get("challenge_id").get("$uuid").asText()))
                    .languageId(UUID.fromString(field.get("language_id").get("$uuid").asText()))
                    .bookmarked(field.get("bookmarked").asBoolean())
                    .score(field.get("score").asInt())
                    .status(field.get("status").asText())
                    .build();

            JsonNode solutionNode = field.get("solution");
            List<SolutionDocument> solutions = new ArrayList<>();

            for(JsonNode solutionField : solutionNode){

                testSolutionDocumentUuid = UUID.fromString(solutionField.get("_id").get("$uuid").asText());

                SolutionDocument builtSolution = new SolutionDocument(
                        testSolutionDocumentUuid,
                        solutionField.get("solution_text").asText()
                );
            }
            userSolution.setSolutionDocument(solutions);

            userSolutions.add(userSolution);
        }
    }

    @BeforeEach
    void setup(){

        userSolutionRepository.deleteAll();

        userSolutionRepository.saveAll(Flux.fromIterable(userSolutions)).blockLast();
    }

    @DisplayName("Repository not null Test")
    @Test
    void testRepositoryNotNull(){

        assertNotNull(userSolutionRepository);
    }

    @DisplayName("Find UserSolutionDocument by UUID test")
    @Test
    void testFindByUuid() {

        Mono<UserSolutionDocument> solutionsFound = userSolutionRepository.findByUuid(testUuid);
        solutionsFound.blockOptional().ifPresentOrElse(
                solutions -> assertEquals(solutions.getUuid(), testUuid),
                () -> fail("Solutions with ID " + testUuid + " not found"));
    }

    @DisplayName("Exists by UUID test")
    @Test
    void testExistsByUuid(){

        Boolean exists = userSolutionRepository.existsByUuid(testUuid).block();
        assertEquals(true, exists);
    }

    @DisplayName("Find UserSolutionDocument by UserId test")
    @Test
    void testFindByUserId(){

        Flux<UserSolutionDocument> solutionsFound = userSolutionRepository.findByUserId(testUserUuid);
        StepVerifier.create(solutionsFound)
                .expectNextMatches(userSolution -> userSolution.getUserId().equals(testUserUuid))
                .thenCancel()
                .verify();
    }

    @DisplayName("Find UserSolutionDocument by ChallengeId test")
    @Test
    void testFindByChallengeId(){

        Flux<UserSolutionDocument> solutionsFound = userSolutionRepository.findByChallengeId(testChallengeUuid);
        StepVerifier.create(solutionsFound)
                .expectNextMatches(userSolution -> userSolution.getChallengeId().equals(testChallengeUuid))
                .thenCancel()
                .verify();
    }

    @DisplayName("Find UserSolutionDocument by LanguageId test")
    @Test
    void testFindByLanguageId(){

        Flux<UserSolutionDocument> solutionsFound = userSolutionRepository.findByLanguageId(testLanguageUuid);
        StepVerifier.create(solutionsFound)
                .expectNextMatches(userSolution -> userSolution.getLanguageId().equals(testLanguageUuid))
                .thenCancel()
                .verify();
    }

    @DisplayName("Find all UserSolutionDocument by bookmarked test")
    @Test
    void testFindByBookmarked(){

        Flux<UserSolutionDocument> solutionsFound = userSolutionRepository.findByBookmarked(true);
        StepVerifier.create(solutionsFound)
                .expectNextCount(allBookmarked)
                .verifyComplete();
    }

    @DisplayName("Find all UserSolutionDocument by status test")
    @Test
    void testFindByStatus(){

        Flux<UserSolutionDocument> solutionsFound = userSolutionRepository.findByStatus("medium");
        StepVerifier.create(solutionsFound)
                .expectNextCount(allMedium)
                .verifyComplete();
    }

    @DisplayName("Find UserSolutionDocument by score test")
    @Test
    void testFindByScore(){

        Flux<UserSolutionDocument> solutionsFound = userSolutionRepository.findByScore(score);
        StepVerifier.create(solutionsFound)
                .expectNextMatches(userSolution -> userSolution.getScore() == score) //at least one result
                .verifyComplete();
    }

    @DisplayName("Find all UserSolutionDocument")
    @Test
    void testFindAll(){

        Flux<UserSolutionDocument> solutionsFound = userSolutionRepository.findAll();

        StepVerifier.create(solutionsFound)
                .expectNextCount(all)
                .verifyComplete();
    }

    @DisplayName("Delete UserSolutionDocument by UUID test")
    @Test
    void testDelete() {

        Mono<Void> solutionDeleted = userSolutionRepository.deleteById(testUuid);
        StepVerifier.create(solutionDeleted)
                .verifyComplete();

        UserSolutionDocument solution = userSolutionRepository.findByUuid(testUuid).block();

        assertNull(solution);
    }
}
