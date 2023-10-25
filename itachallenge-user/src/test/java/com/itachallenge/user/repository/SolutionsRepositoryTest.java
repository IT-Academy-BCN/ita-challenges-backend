package com.itachallenge.user.repository;

import com.itachallenge.user.document.Solution;
import com.itachallenge.user.document.UserSolutions;
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
public class SolutionsRepositoryTest {

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
    private IUserSolutionsRepository userSolutionsRepository;

    private static List<UserSolutions> userSolutions;

    private static UUID testUuid;
    private static UUID testUserUuid;
    private static UUID testChallengeUuid;
    private static UUID testLanguageUuid;
    private static int allMedium;
    private static int allBookmarked;
    private static int score;
    private static int all;

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


            UserSolutions userSolution = UserSolutions.builder()
                                                .uuid(UUID.fromString(field.get("_id").get("$uuid").asText()))
                                                .userId(UUID.fromString(field.get("user_id").get("$uuid").asText()))
                                                .challengeId(UUID.fromString(field.get("challenge_id").get("$uuid").asText()))
                                                .languageId(UUID.fromString(field.get("language_id").get("$uuid").asText()))
                                                .bookmarked(field.get("bookmarked").asBoolean())
                                                .score(field.get("score").asInt())
                                                .status(field.get("status").asText())
                                                .build();

            JsonNode solutionNode = field.get("solution");
            Solution[] solutions = new Solution[3];
            int counter = 0;

            for(JsonNode solutionField : solutionNode){

                Solution builtSolution = Solution.builder()
                                                .uuid(UUID.fromString(solutionField.get("_id").get("$uuid").asText()))
                                                .solutionText(solutionField.get("solution_text").asText())
                                                .build();

                solutions[counter] = builtSolution;
                counter++;
            }
            userSolution.setSolution(solutions);

            userSolutions.add(userSolution);
        }
    }

    @BeforeEach
    public void setup(){

        userSolutionsRepository.deleteAll();

        userSolutionsRepository.saveAll(Flux.fromIterable(userSolutions)).blockLast();
    }

    @DisplayName("Repository not null Test")
    @Test
    public void testRepositoryNotNull(){

        assertNotNull(userSolutionsRepository);
    }

    @DisplayName("Find UserSolution by UUID test")
    @Test
    public void testFindByUuid() {

        Mono<UserSolutions> solutionsFound = userSolutionsRepository.findByUuid(testUuid);
        solutionsFound.blockOptional().ifPresentOrElse(
                solutions -> assertEquals(solutions.getUuid(), testUuid),
                () -> fail("Solutions with ID " + testUuid + " not found"));
    }

    @DisplayName("Exists by UUID test")
    @Test
    public void testExistsByUuid(){

        Boolean exists = userSolutionsRepository.existsByUuid(testUuid).block();
        assertEquals(true, exists);
    }

    @DisplayName("Find UserSolution by UserId test")
    @Test
    public void testFindByUserId(){

        Flux<UserSolutions> solutionsFound = userSolutionsRepository.findByUserId(testUserUuid);
        StepVerifier.create(solutionsFound)
                .expectNextMatches(userSolution -> userSolution.getUserId().equals(testUserUuid))
                .thenCancel()
                .verify();
    }

    @DisplayName("Find UserSolution by ChallengeId test")
    @Test
    public void testFindByChallengeId(){

        Flux<UserSolutions> solutionsFound = userSolutionsRepository.findByChallengeId(testChallengeUuid);
        StepVerifier.create(solutionsFound)
                .expectNextMatches(userSolution -> userSolution.getChallengeId().equals(testChallengeUuid))
                .thenCancel()
                .verify();
    }

    @DisplayName("Find UserSolution by LanguageId test")
    @Test
    public void testFindByLanguageId(){

        Flux<UserSolutions> solutionsFound = userSolutionsRepository.findByLanguageId(testLanguageUuid);
        StepVerifier.create(solutionsFound)
                .expectNextMatches(userSolution -> userSolution.getLanguageId().equals(testLanguageUuid))
                .thenCancel()
                .verify();
    }

    @DisplayName("Find all UserSolutions by bookmarked test")
    @Test
    public void testFindByBookmarked(){

        Flux<UserSolutions> solutionsFound = userSolutionsRepository.findByBookmarked(true);
        StepVerifier.create(solutionsFound)
                .expectNextCount(allBookmarked)
                .verifyComplete();
    }

    @DisplayName("Find all UserSolutions by status test")
    @Test
    public void testFindByStatus(){

        Flux<UserSolutions> solutionsFound = userSolutionsRepository.findByStatus("medium");
        StepVerifier.create(solutionsFound)
                .expectNextCount(allMedium)
                .verifyComplete();
    }

    @DisplayName("Find UserSolutions by score test")
    @Test
    public void testFindByScore(){

        Flux<UserSolutions> solutionsFound = userSolutionsRepository.findByScore(score);
        StepVerifier.create(solutionsFound)
                .expectNextMatches(userSolution -> userSolution.getScore() == score) //at least one result
                .verifyComplete();
    }

    @DisplayName("Find all UserSolutions")
    @Test
    public void testFindAll(){

        Flux<UserSolutions> solutionsFound = userSolutionsRepository.findAll();

        StepVerifier.create(solutionsFound)
                .expectNextCount(all)
                .verifyComplete();
    }

    @DisplayName("Delete UserSolutions by UUID test")
    @Test
    public void testDelete() {

        Mono<Void> solutionDeleted = userSolutionsRepository.deleteById(testUuid);
        StepVerifier.create(solutionDeleted)
                .verifyComplete();

        UserSolutions solution = userSolutionsRepository.findByUuid(testUuid).block();

        assertNull(solution);
    }
}
