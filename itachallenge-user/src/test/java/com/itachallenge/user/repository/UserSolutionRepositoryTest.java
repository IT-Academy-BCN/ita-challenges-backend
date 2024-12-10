package com.itachallenge.user.repository;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.enums.ChallengeStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
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
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserSolutionRepositoryTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withExposedPorts(27017);

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
    private static ChallengeStatus testStatus;
    private static int allBookmarked;
    private static int score;
    private static int all;
    private static UUID testSolutionDocumentUuid;

    UUID uuid_1 = UUID.fromString("8ecbfe54-fec8-11ed-be56-0242ac120001");
    UUID uuid_2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120002");
    UUID uuid_3 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");
    UUID userId1 = UUID.fromString("442b8e6e-5d57-4d12-9be2-3ff4f26e7d79");
    UUID userId2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");

    private int scoreZmq;
    private String errorsZmq;
    private UserSolutionDocument userSolutionZmq;

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
            testStatus = ChallengeStatus.valueOf(field.get("status").asText().toUpperCase());
            score = field.get("score").asInt();
            all++;


            UserSolutionDocument userSolution = UserSolutionDocument.builder()
                    .uuid(UUID.fromString(field.get("_id").get("$uuid").asText()))
                    .userId(UUID.fromString(field.get("user_id").get("$uuid").asText()))
                    .challengeId(UUID.fromString(field.get("challenge_id").get("$uuid").asText()))
                    .languageId(UUID.fromString(field.get("language_id").get("$uuid").asText()))
                    .bookmarked(field.get("bookmarked").asBoolean())
                    .score(field.get("score").asInt())
                    .status(ChallengeStatus.valueOf(field.get("status").asText().toUpperCase()))
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

        all += 3;
        allBookmarked += 3;
        /* 16/10/2024 Michel: Para que cuadren los tests al refactorizar todos en 1 sola clase
        ** para que no colisione el desarrollo de Aebal con el de Dario */
    }

    @BeforeEach
    void setup(){

        userSolutionRepository.deleteAll().block();

        userSolutionRepository.saveAll(Flux.fromIterable(userSolutions)).blockLast();

        UUID challengeId1 = UUID.fromString("7fc6a737-dc36-4e1b-87f3-120d81c548aa");
        UUID challengeId2 = UUID.fromString("5c1a97e5-1cca-4144-9981-2de1fb73b178");

        UUID languageId1 = UUID.fromString("1e047ea2-b787-49e7-acea-d79e92be3909");
        UUID languageId2 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");

        UUID solutionId1 = UUID.fromString("1e047ea2-b787-49e7-acea-d79e92be3909");
        UUID solutionId2 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");

        String solutionText1 = "Ipsum.. 1";
        String solutionText2 = "Ipsum.. 2";

        SolutionDocument solutionDocument1 = new SolutionDocument(solutionId1, solutionText1);
        SolutionDocument solutionDocument2 = new SolutionDocument(solutionId2, solutionText2);

        List<SolutionDocument> solutionDocumentList = List.of(solutionDocument1, solutionDocument2);

        UserSolutionDocument userScoreDocument1 = new UserSolutionDocument(uuid_1, userId1, challengeId1, languageId1, true, ChallengeStatus.STARTED, 90, "xxx", solutionDocumentList);
        UserSolutionDocument userScoreDocument2 = new UserSolutionDocument(uuid_2, userId2, challengeId2, languageId2, true, ChallengeStatus.STARTED, 90, "xxx", solutionDocumentList);
        UserSolutionDocument userScoreDocument3 = new UserSolutionDocument(uuid_3, userId1, challengeId1, languageId1, true, ChallengeStatus.STARTED, 90, "xxx", solutionDocumentList);

        userSolutionRepository.saveAll(Flux.just(userScoreDocument1, userScoreDocument2, userScoreDocument3)).blockLast();

        // Para testear ZMQ
        scoreZmq = 100;
        errorsZmq = "Error test";
        userSolutionZmq = UserSolutionDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .challengeId(UUID.randomUUID())
                .languageId(UUID.randomUUID())
                .solutionDocument(solutionDocumentList)
                .build();
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

        Flux<UserSolutionDocument> solutionsFound = userSolutionRepository.findByStatus(testStatus);
        StepVerifier.create(solutionsFound)
                .thenConsumeWhile(userSolutionDocument -> userSolutionDocument.getStatus().equals(testStatus))
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

    @DisplayName("Count number of BookmarkedTrue by idChallenge")
    @Test
    void testCountBookmarkedTrueByChallengeId(){
        boolean isBookmarked = true;
        Mono<Long> numberOfBookmarks = userSolutionRepository.countByChallengeIdAndBookmarked(testChallengeUuid, isBookmarked);
        long expectedValue = 1L;
        StepVerifier.create(numberOfBookmarks)
                .expectNextCount(expectedValue)
                .verifyComplete();
    }


    @DisplayName("Return solutions by idChallenge and status")
    @Test
    void testFindByChallengeIdAndStatus() {

        Flux<UserSolutionDocument> solutionsFound = userSolutionRepository.findByChallengeIdAndStatus(testChallengeUuid, testStatus);

        StepVerifier.create(solutionsFound)
                .expectNextMatches(userSolution ->
                        userSolution.getChallengeId().equals(testChallengeUuid) &&
                        userSolution.getStatus().equals(testStatus))
                .thenCancel()
                .verify();

    }

    @DisplayName("Repository not null Test")
    @Test
    void testDB(){
        assertNotNull(userSolutionRepository);
    }

    @DisplayName("Count users by id")
    @Test
    void CountUserById(){
        Flux<UserSolutionDocument> userScoreDocTest = userSolutionRepository.findByUserId(userId1);

        StepVerifier.create(userScoreDocTest)
                .expectNextCount(3)
                .verifyComplete();
    }

    @DisplayName("Should persist score and errors in existing document when receiving data from ZMQ")
    @Test
    void testSaveScoreAndErrorsInExistingDocument() {

        Mono<UserSolutionDocument> savedInitialDocument = userSolutionRepository.save(userSolutionZmq);

        StepVerifier.create(savedInitialDocument)
                .expectNextMatches(doc -> doc.getScore() == 0 && (doc.getErrors() == null || doc.getErrors().isEmpty()))
                .verifyComplete();

        Mono<UserSolutionDocument> existingDocument = userSolutionRepository.findByUuid(userSolutionZmq.getUuid());

        Mono<UserSolutionDocument> updatedDocument = existingDocument
                .map(doc -> {
                    doc.setScore(scoreZmq);
                    doc.setErrors(errorsZmq);
                    return doc;
                })
                .flatMap(userSolutionRepository::save);

        StepVerifier.create(updatedDocument)
                .expectNextMatches(doc -> doc.getScore() == scoreZmq && doc.getErrors().equals(errorsZmq))
                .verifyComplete();

        StepVerifier.create(userSolutionRepository.findByUuid(userSolutionZmq.getUuid()))
                .expectNextMatches(doc -> doc.getScore() == scoreZmq && doc.getErrors().equals(errorsZmq))
                .verifyComplete();
    }
}