package com.itachallenge.user.repository;

import com.itachallenge.user.document.User;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DataMongoTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@PropertySource("classpath:persistence-test.properties")
public class UserRepositoryTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo")
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void initMongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("container url: {}" + container.getReplicaSetUrl("itac-users"));
        System.out.println("container host/port: {}/{}" + container.getHost() + " - " + container.getFirstMappedPort());

        registry.add("spring.data.mongodb.uri", () -> container.getReplicaSetUrl("itac-users"));
    }

    @Autowired
    private IUserRepository userRepository;

    private User user, user2;
    private UUID uuid, uuid2;
    private String email, email2;

    @BeforeEach
    public void setup() {
        String uuidString = "4020e1e1-e6b2-4c20-817b-70193b518b3f";
        uuid = UUID.fromString(uuidString);
        String uuidString2 = "5020e1e1-e6b2-4c20-817b-70193b518b3f";
        uuid2 = UUID.fromString(uuidString2);
        email = "test@example.com";
        email2 = "test2@example.com";

        user = User.builder()
                .uuid(uuid)
                .name("Maria")
                .surname("Lopez")
                .nickname("marieta")
                .email(email)
                .password("testpassword").build();

        user2 = User.builder()
                .uuid(uuid2)
                .name("Joana")
                .surname("Perez")
                .nickname("joaneta")
                .email(email2)
                .password("testpassword2").build();

        userRepository.deleteAll().block();
        userRepository.saveAll(Flux.just(user, user2)).blockLast();
    }
    @Test
    public void testFindByUuid() {

        Mono<User> userFound = userRepository.findByUuid(uuid);
        userFound.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getUuid(), uuid),
                () -> fail("User with ID " + uuid + " not found"));

        Mono<User> user2Found = userRepository.findByUuid(uuid2);
        user2Found.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getUuid(), uuid2),
                () -> fail("User with ID " + uuid2 + " not found"));
    }

    @Test
    public void testFindByEmail() {

        Mono<User> userFound = userRepository.findByEmail(email);
        userFound.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getEmail(), email),
                () -> fail("User with email " + email + " not found"));

        Mono<User> user2Found = userRepository.findByEmail(email2);
        user2Found.blockOptional().ifPresentOrElse(
                u -> assertEquals(u.getEmail(), email2),
                () -> fail("User with email " + email2 + " not found"));
    }

    @Test
    public void testExistsByUuid() {
        Boolean exists = userRepository.existsByUuid(uuid).block();
        assertEquals(exists, true);

        Boolean exists2 = userRepository.existsByUuid(uuid2).block();
        assertEquals(exists2, true);
    }

    @Test
    public void testExistsByEmail() {
        Boolean exists = userRepository.existsByEmail(email).block();
        assertEquals(exists, true);

        Boolean exists2 = userRepository.existsByEmail(email2).block();
        assertEquals(exists2, true);
    }

}
