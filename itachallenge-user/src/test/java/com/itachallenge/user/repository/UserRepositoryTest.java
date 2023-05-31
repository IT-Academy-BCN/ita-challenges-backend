package com.itachallenge.user.repository;

import com.itachallenge.user.document.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.util.UUID;
import java.util.function.Predicate;

@DataMongoTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml")
public class UserRepositoryTest {

    @Autowired
    private IUserRepository userRepository;

    private User user, user2;
    private UUID uuid, uuid2;
    private String email, email2;

    @BeforeAll
    public void setup() {
        uuid = UUID.randomUUID();
        uuid2 = UUID.randomUUID();
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
    }
    @Test
    public void testFindByUuid() {

        Publisher<User> setup = this.userRepository.deleteAll()
                .thenMany(this.userRepository.saveAll(Flux.just(user, user2)))
                .thenMany(this.userRepository.findByUuid(uuid2));

        Predicate<User> userPredicate = user -> uuid2.equals(user2.getUuid());

        StepVerifier.create(setup)
                .expectNextMatches(userPredicate)
                .verifyComplete();
    }
    @Test
    public void testFindByEmail() {

        Publisher<User> setup = this.userRepository.deleteAll()
                .thenMany(this.userRepository.saveAll(Flux.just(user, user2)))
                .thenMany(this.userRepository.findByEmail(email));

        Predicate<User> userPredicate = user -> email.equals(user.getEmail());

        StepVerifier.create(setup)
                .expectNextMatches(userPredicate)
                .verifyComplete();
    }

}
