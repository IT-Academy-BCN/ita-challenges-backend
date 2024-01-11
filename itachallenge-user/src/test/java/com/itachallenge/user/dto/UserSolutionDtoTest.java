package com.itachallenge.user.dto;

import com.itachallenge.user.dtos.UserSolutionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class UserSolutionDtoTest {

    private UserSolutionDto userSolutionDto;

    @BeforeEach
    public void setUp() {
        userSolutionDto = UserSolutionDto.builder()
                .userId("validUserId")
                .challengeId("validChallengeId")
                .languageId("validLanguageId")
                .solutionText("Valid solution text")
                .build();
    }

    @Test
    void testUserSolutionDto() {
        assertNotNull(userSolutionDto);
        assertEquals("validUserId", userSolutionDto.getUserId());
        assertEquals("validChallengeId", userSolutionDto.getChallengeId());
        assertEquals("validLanguageId", userSolutionDto.getLanguageId());
        assertEquals("Valid solution text", userSolutionDto.getSolutionText());
    }

    @Test
    void testInvalidUserId() {
        userSolutionDto.setUserId("invalidUserId");
        assertEquals("invalidUserId", userSolutionDto.getUserId());
    }

    @Test
    void testInvalidSolutionText() {
        userSolutionDto.setSolutionText("");
        assertEquals("", userSolutionDto.getSolutionText());
    }
}