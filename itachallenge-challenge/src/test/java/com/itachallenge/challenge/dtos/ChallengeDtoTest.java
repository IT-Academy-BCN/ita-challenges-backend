package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeDto.class)
class ChallengeDtoTest {
    private final static UUID ID = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testChallengeDto() {
        ChallengeDto challengeDto = new ChallengeDto(ID);

        assertEquals(ID, challengeDto.getUuid());
    }

    @Test
    void testJsonPropertyAnnotation() throws JsonProcessingException {
        ChallengeDto challengeDto = new ChallengeDto();
        challengeDto.setUuid(ID);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(challengeDto);

        assertTrue(json.contains("id_challenge"));

        ChallengeDto deserializedDto = objectMapper.readValue(json, ChallengeDto.class);
        assertEquals(ID, deserializedDto.getUuid());
    }

}
