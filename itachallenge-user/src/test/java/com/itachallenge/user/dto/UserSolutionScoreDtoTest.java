package com.itachallenge.user.dto;

import com.itachallenge.user.dtos.UserSolutionScoreDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserSolutionScoreDtoTest {

    private UserSolutionScoreDto userSolutionScoreDto;
    @BeforeEach
    public void setUp() {

    userSolutionScoreDto = UserSolutionScoreDto.builder()
            .userId("validUserId")
            .challengeId("validChallengeId")
            .languageId("validLanguageId")
            .solutionText("Valid solution text")
            .score(80)
            .build();
}
    @Test
    void testUserSolutionScoreDto() {
        assertNotNull(userSolutionScoreDto);
        assertEquals("validUserId", userSolutionScoreDto.getUserId());
        assertEquals("validChallengeId", userSolutionScoreDto.getChallengeId());
        assertEquals("validLanguageId", userSolutionScoreDto.getLanguageId());
        assertEquals("Valid solution text", userSolutionScoreDto.getSolutionText());
        assertEquals(80, userSolutionScoreDto.getScore());
    }
}