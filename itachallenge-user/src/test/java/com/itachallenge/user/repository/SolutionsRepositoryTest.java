package com.itachallenge.user.repository;

import com.itachallenge.user.config.ConvertersConfig;
import com.itachallenge.user.document.Solutions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@PropertySource("classpath:persistence-test.properties")
@Import(ConvertersConfig.class)
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
    private SolutionRepository solutionRepository;

    @BeforeEach
    public void setup() throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder(
                "mongoimport",
                "--uri", container.getReplicaSetUrl("users"),
                "--db", "users",
                "--collection", "solutions",
                "--jsonArray",
                "--file", "user_score.json"
        ).directory(new File("src/test/resources"));

        Process process = processBuilder.start();

       try{
           int exitCode = process.waitFor();

           if (exitCode == 0) {
               System.out.println("Successful import");
           } else {
               System.err.println("Import error");
           }

       }catch(Exception e){
           e.printStackTrace();
       }
    }

    private final UUID uuid = UUID.fromString("a75fa943-6bc9-4e62-b1db-d43d15b149c7");

    private final UUID uuid2 = UUID.fromString("1e8f64e2-6d68-4ae1-9425-8102d0ab61d3");

    private final UUID uuid3 = UUID.fromString("956c9dbd-2e85-4d68-a75b-9783d83c7f72");

    @Test
    public void testFindByUuid() {

        System.out.println(uuid);
        System.out.println(uuid2);
        System.out.println(uuid3);

        System.out.println(solutionRepository.findAll().blockFirst());
        System.out.println(solutionRepository.findAll().blockFirst().getUuid());

        System.out.println(solutionRepository.findById(uuid).block());

        Mono<Solutions> solutionsFound = solutionRepository.findByUuid(uuid);
        solutionsFound.blockOptional().ifPresentOrElse(
                solutions -> assertEquals(solutions.getUuid(), uuid),
                () -> fail("Solutions with ID " + uuid + " not found"));
    }

    @Test
    public void testFindByStatus(){

        Flux<Solutions> solutionsFound = solutionRepository.findByStatus("medium");

        System.out.println(solutionsFound.collectList().block());
    }

    @Test
    public void testFindByUuid2(){

        Mono<Solutions> solutionsFound = solutionRepository.findByUuid(uuid2);
        solutionsFound.blockOptional().ifPresentOrElse(
                solutions -> assertEquals(solutions.getUuid(), uuid2),
                () -> fail("Solutions with ID " + uuid2 + " not found"));
    }

    @Test
    public void testFindByUuid3(){

        Mono<Solutions> solutionsFound = solutionRepository.findByUuid(uuid3);
        solutionsFound.blockOptional().ifPresentOrElse(
                solutions -> assertEquals(solutions.getUuid(), uuid3),
                () -> fail("Solutions with ID " + uuid3 + " not found"));
    }

    @Test
    public void testFindAll(){

        Flux<Solutions> solutionsFound = solutionRepository.findAll();

        StepVerifier.create(solutionsFound)
                .expectNextCount(4)
                .verifyComplete();
    }

    @DisplayName("Repository not null Test")
    @Test
    public void testRepositoryNotNull(){

        Assertions.assertNotNull(solutionRepository);
    }
}
