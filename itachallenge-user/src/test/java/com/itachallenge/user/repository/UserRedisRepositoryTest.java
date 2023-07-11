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
import reactor.core.publisher.Mono;

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
    private UUID uuid1, uuid2;

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
        userRedisRepository.deleteAll();

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
                .roles(List.of(Role.ADMIN))
                .password(passwordEncoder.encode("playaVirginia"))
                .build();

        this.uuid1 = user1.getUuid();
        this.uuid2 = user2.getUuid();

        userRedisRepository.save(user1);
        userRedisRepository.save(user2);
    }

    @Test
    @DisplayName("Delete User")
    void shouldDeleteUser(){
        userRedisRepository.deleteByUuid(user1.getUuid()).block();

        userRedisRepository.findByUuid(user1.getUuid()).blockOptional().ifPresent(u -> fail("The user hasn't been deleted"));

    }

}
