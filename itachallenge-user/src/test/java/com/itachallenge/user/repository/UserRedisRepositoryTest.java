package com.itachallenge.user.repository;

import com.itachallenge.user.document.Role;
import com.itachallenge.user.hash.UserHash;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

@Testcontainers
@SpringBootTest
class UserRedisRepositoryTest {

    @Container
    static GenericContainer redisContainer = new GenericContainer("redis:latest")
            .withExposedPorts(6397);

    @Autowired
    private UserRedisRepository userRedisRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserHash user1, user2;
    private UUID uuid1, uuid2;

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

        userRedisRepository.save(user1);
        userRedisRepository.save(user2);
    }

}
