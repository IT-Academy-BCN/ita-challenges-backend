package com.itachallenge.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void testLombokGeneratedMethods() {
        UserSolutionScoreDto dto1 = UserSolutionScoreDto.builder().build();
        UserSolutionScoreDto dto2 = UserSolutionScoreDto.builder().build();

        assertThat(dto1).isNotNull();
        assertThat(dto1.toString()).isNotEmpty();
        assertThat(dto1.hashCode()).isNotZero();
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.getClass()).isEqualTo(UserSolutionScoreDto.class);
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