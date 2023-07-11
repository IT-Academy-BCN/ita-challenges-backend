package com.itachallenge.user.repository;

import com.itachallenge.user.document.Role;
import com.itachallenge.user.hash.UserHash;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class UserRedisRepositoryTest {

    @Container
    static GenericContainer redisContainer = new GenericContainer("redis:latest")
            .withExposedPorts(6379);

    @Autowired
    private UserRedisRepository userRedisRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserHash user1, user2;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("redis.port",redisContainer::getFirstMappedPort);
        registry.add("redis.host",redisContainer::getHost);
        registry.add("redis.user.key", () ->"USER");
    }

    @BeforeAll
    static void beforeAll() {
        redisContainer.start();
    }

    @AfterAll
    static void afterAll() {
        redisContainer.stop();
    }

    @BeforeEach
    void setUp(){
        userRedisRepository.deleteAll().block();

        user1 = UserHash.builder()
                .uuid(UUID.fromString("5cd86107-ef39-4a24-b6ee-77d5bd176e2d"))
                .email("fernando@seko.com")
                .name("Fernando")
                .surname("Galvez")
                .nickname("Seko")
                .roles(List.of(Role.ADMIN))
                .password(passwordEncoder.encode("takers"))
                .build();

        user2 = UserHash.builder()
                .uuid(UUID.fromString("fad17eab-1ab2-484c-8bf5-04e8b8fcb561"))
                .email("Dani@delaossa.com")
                .name("Dani")
                .surname("Delaossa")
                .nickname("Della")
                .roles(List.of(Role.ADMIN))
                .password(passwordEncoder.encode("playaVirginia"))
                .build();

        userRedisRepository.save(user1).block();
        userRedisRepository.save(user2).block();
    }

    @Test
    @DisplayName("Should Delete User")
    void shouldDeleteUser(){
        //when
        userRedisRepository.deleteByUuid(user1.getUuid()).block();

        //should
        userRedisRepository.findByUuid(user1.getUuid()).blockOptional().ifPresent(u -> fail("The user hasn't been deleted"));
    }

    @Test
    @DisplayName("Should Save User")
    void shouldSaveUser(){
        UUID uuid = UUID.randomUUID();
        UserHash userHash = UserHash.builder()
                .uuid(uuid)
                .name("Test")
                .surname("Test2")
                .roles(List.of(Role.USER))
                .nickname("TestJR")
                .email("test@dummy.com")
                .password(passwordEncoder.encode("dummyPass"))
                .build();


        userRedisRepository.save(userHash).block();
        Mono<UserHash> found = userRedisRepository.findByUuid(uuid);

        StepVerifier.create(found)
                .expectNext(userHash)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should Exist")
    void shouldExist(){
        Mono<Boolean> exist = userRedisRepository.existsByUuid(user2.getUuid());
        StepVerifier.create(exist)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should Not Exist")
    void shouldNotExist(){
        Mono<Boolean> exist = userRedisRepository.existsByUuid(UUID.randomUUID());
        StepVerifier.create(exist)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find by uuid")
    void shouldFindByUuid(){
        Mono<UserHash> found = userRedisRepository.findByUuid(user2.getUuid());
        StepVerifier.create(found)
                .expectNext(user2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should not find by uuid")
    void shouldNotFindByUuid(){
        Mono<UserHash> found = userRedisRepository.findByUuid(UUID.randomUUID());
        StepVerifier.create(found)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should delete all")
    void shouldDeleteAll(){
        userRedisRepository.deleteAll().block();
        Flux<UserHash> shouldBeEmpty = userRedisRepository.findAll();
        StepVerifier.create(shouldBeEmpty)
                .expectNextCount(0)
                .verifyComplete();
    }






}
