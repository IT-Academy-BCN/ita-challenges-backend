package com.itachallenge.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserSolutionScoreDtoTest {

    private UserSolutionScoreDto userSolutionScoreDto;
    String userId = UUID.randomUUID().toString();
    String challengeId = UUID.randomUUID().toString();
    String languageId = UUID.randomUUID().toString();
    int score = 30;
    String solutionText = "This is my solution";
    UserSolutionScoreDto solutionScoreDto = new UserSolutionScoreDto();
    UserSolutionScoreDto dto1 = UserSolutionScoreDto.builder().build();

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
    void lombokGeneratedMethods_test() {
        assertThat(dto1).isNotNull();
        assertThat(dto1.getClass()).isEqualTo(UserSolutionScoreDto.class);
    }

    @Test
    void getterUserSolutionScoreDto_test() {
        assertNotNull(userSolutionScoreDto);
        assertEquals("validUserId", userSolutionScoreDto.getUserId());
        assertEquals("validChallengeId", userSolutionScoreDto.getChallengeId());
        assertEquals("validLanguageId", userSolutionScoreDto.getLanguageId());
        assertEquals("Valid solution text", userSolutionScoreDto.getSolutionText());
        assertEquals(80, userSolutionScoreDto.getScore());
    }

    @Test
    void noArgsConstructor_GetterAndSetter_UserSolutionScoreDto_test(){
        solutionScoreDto.setUserId(userId);
        solutionScoreDto.setChallengeId(challengeId);
        solutionScoreDto.setLanguageId(languageId);
        solutionScoreDto.setScore(score);
        solutionScoreDto.setSolutionText(solutionText);

        assertThat(solutionScoreDto.getUserId()).isEqualTo(userId);
        assertThat(solutionScoreDto.getChallengeId()).isEqualTo(challengeId);
        assertThat(solutionScoreDto.getLanguageId()).isEqualTo(languageId);
        assertThat(solutionScoreDto.getScore()).isEqualTo(score);
        assertThat(solutionScoreDto.getSolutionText()).isEqualTo(solutionText);
    }

    @Test
    void jsonSerialization_test() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userSolutionScoreDto);
        assertTrue(json.contains("\"uuid_user\":\"validUserId\""));
        assertTrue(json.contains("\"uuid_language\":\"validLanguageId\""));
        assertTrue(json.contains("\"uuid_challenge\":\"validChallengeId\""));
        assertTrue(json.contains("\"solution_text\":\"Valid solution text\""));
        assertTrue(json.contains("\"score\":80"));
    }

    @Test
    void requiredArgsConstructor_userSolutionScoreDto_test(){
        UserSolutionScoreDto userSolutionScoreDto1 = new UserSolutionScoreDto(
                userId, challengeId, languageId, solutionText, score);
        assertThat(userSolutionScoreDto1.getUserId()).isEqualTo(userId);
        assertThat(userSolutionScoreDto1.getChallengeId()).isEqualTo(challengeId);
        assertThat(userSolutionScoreDto1.getLanguageId()).isEqualTo(languageId);
        assertThat(userSolutionScoreDto1.getScore()).isEqualTo(score);
        assertThat(userSolutionScoreDto1.getSolutionText()).isEqualTo(solutionText);
    }
}