package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = Challenge.class)
class challengeTest {
    private final static UUID ID = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testChallenge() {
        Challenge challenge = new Challenge(ID);

        assertEquals(ID, challenge.getUuid());
    }
}
