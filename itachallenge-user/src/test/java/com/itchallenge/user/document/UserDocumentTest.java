package com.itchallenge.user.document;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@EnableReactiveMongoRepositories
class UserDocumentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
    }

    @Test
    void shouldVerifySchemaHasUsernameFieldAndIsUnique() {
        UserDocument user = new UserDocument(UUID.randomUUID(), "TestUser");
        userRepository.save(user).block();

        Mono<UserDocument> foundUser = userRepository.findByUsername("TestUser");
        StepVerifier.create(foundUser)
                .assertNext(u -> assertThat(u.getUsername()).isEqualTo("TestUser"))
                .verifyComplete();
    }

    @Test
    void shouldManuallyRegisterMentorsAndEnsureUsernameFormat() {
        UserDocument mentor1 = new UserDocument(UUID.randomUUID(), "Mentor123");
        UserDocument mentor2 = new UserDocument(UUID.randomUUID(), "mentor456");

        Mono<Void> saveMentors = userRepository.save(mentor1)
                .then(userRepository.save(mentor2))
                .then();

        StepVerifier.create(saveMentors).verifyComplete();

        StepVerifier.create(userRepository.findAll().collectList())
                .assertNext(users -> {
                    assertThat(users).hasSize(2);
                    assertThat(users).allMatch(user -> user.getUsername().matches("^[a-zA-Z0-9]+$"));
                })
                .verifyComplete();
    }

    @Test
    void shouldNotAllowDuplicateUsernames() {
        UserDocument mentor = new UserDocument(UUID.randomUUID(), "UniqueMentor");
        UserDocument duplicateMentor = new UserDocument(UUID.randomUUID(), "UniqueMentor");

        Mono<Void> testMono = userRepository.save(mentor)
                .then(userRepository.save(duplicateMentor))
                .then();

        StepVerifier.create(testMono)
                .expectError(org.springframework.dao.DuplicateKeyException.class)
                .verify();

        StepVerifier.create(userRepository.findByUsername("UniqueMentor"))
                .assertNext(user -> assertThat(user).isNotNull())
                .verifyComplete();
    }
}
