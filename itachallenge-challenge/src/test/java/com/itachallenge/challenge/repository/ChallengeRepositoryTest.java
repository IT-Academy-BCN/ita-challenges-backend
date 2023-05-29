package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.junit.Assert.*;
import static org.springframework.test.util.AssertionErrors.fail;

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ChallengeRepositoryTest {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("container url: {}" + container.getReplicaSetUrl("challenges"));
        System.out.println("container host/port: {}/{}" + container.getHost() + " - " + container.getFirstMappedPort());

        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("challenges"));
    }



    @BeforeEach
    public void setUp(){
      assertNotNull(challengeRepository);
/*        challengeRepository.deleteAll().block();
        Challenge challenge1 = new Challenge("23", "Loops");
        Challenge challenge2 = new Challenge("24", "If-Else");
        challengeRepository.saveAll(Flux.just(challenge1, challenge2)).blockLast();*/
    }


    @Test
    void testDB() {
/*        Challenge challenge1 = new Challenge();
        System.out.println("=========");
        System.out.println(challenge1);*/
    }

    /*@BeforeEach
    public void setUp(){
        challengeRepository.deleteAll().block();
        Challenge challenge1 = new Challenge("23", "Loops");
        Challenge challenge2 = new Challenge("24", "If-Else");
        challengeRepository.saveAll(Flux.just(challenge1, challenge2)).blockLast();
    }

    @Test
    void findByNameTest(){

        Mono<Challenge> firstChallenge = challengeRepository.findByName("Loops");
        firstChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getName(), "Loops"),
                () -> fail("Challenge with name Loops  not found."));

        Mono<Challenge> secondChallenge = challengeRepository.findByName("If-Else");
        secondChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getName(), "If-Else"),
                () -> fail("Challenge with name If-Else not found." ));
    }


    @Test
    void findByUuidTest(){

        Mono<Challenge> firstChallenge = challengeRepository.findById("23");
        firstChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getChallengeId(), uuid_1),
                () -> fail("Challenge not found: " + uuid_1));

        Mono<Challenge> secondChallenge = challengeRepository.findByUuid(uuid_2);
        secondChallenge.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getChallengeId(), uuid_2),
                () -> fail("Challenge not found: " + uuid_2));
    }

    @Test
    void existsByUuidTest(){
        Challenge exists = challengeRepository.existsByUuid(uuid_1).block();
        assertEquals(exists, true);
    }*/


}
