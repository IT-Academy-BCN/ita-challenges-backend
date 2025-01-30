package com.itchallenge.user.document;

import com.itachallenge.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@DataMongoTest
class UserDocumentTest {

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(userRepository);
    }
}
