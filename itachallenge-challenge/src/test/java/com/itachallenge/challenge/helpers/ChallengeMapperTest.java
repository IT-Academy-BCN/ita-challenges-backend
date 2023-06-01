package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.dtos.ChallengeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeMapper.class)
class ChallengeMapperTest {
    //variables
    private final static UUID ID = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");

    @MockBean
    private ChallengeMapper challengeMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMapToChallengeDto() {
        Challenge challenge = new Challenge();
        challenge.setUuid(ID);
        
        ChallengeDto ChallengeExpectedDto = ChallengeDto.builder()
                .uuid(challenge.getUuid())
                .build();

        when(challengeMapper.mapToChallengeDto(challenge)).thenReturn(ChallengeExpectedDto);

        ChallengeDto challengeDto = challengeMapper.mapToChallengeDto(challenge);
        assertEquals(ChallengeExpectedDto, challengeDto);
    }

}
