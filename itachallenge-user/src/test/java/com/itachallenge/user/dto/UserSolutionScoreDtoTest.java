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
    String user2Id = UUID.randomUUID().toString();
    String user3Id = UUID.randomUUID().toString();
    String challengeId = UUID.randomUUID().toString();
    String languageId = UUID.randomUUID().toString();
    int score = 30;
    String solutionText = "This is my solution";
    UserSolutionScoreDto solutionScoreDto = new UserSolutionScoreDto();
    UserSolutionScoreDto dto1 = UserSolutionScoreDto.builder().build();
    UserSolutionScoreDto dto2 = UserSolutionScoreDto.builder().build();
    UserSolutionScoreDto dto3 = UserSolutionScoreDto.builder().userId(user3Id).build();

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
        assertThat(dto1.toString()).isNotEmpty();
        assertThat(dto3.toString()).contains(user3Id);
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

    @Test
    void equals_test(){
        dto1.setUserId(userId);
        dto2.setUserId(userId);
        assertEquals(dto1, dto2);
    }

    @Test
    void nonEquals_test(){
        dto1.setUserId(userId);
        dto2.setUserId(user2Id);
        assertNotEquals(dto1, dto2);
    }

    @Test
    void hash_test(){
        dto1.setUserId(userId);
        dto2.setUserId(userId);
        dto3.setUserId(user3Id);
        assertThat(dto1.hashCode()).isNotZero();
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(),dto3.hashCode());
        assertNotEquals(dto2.hashCode(),dto3.hashCode());
    }

    @Test
    void toString_test(){
        dto1.setUserId(userId);
        dto3.setUserId(user3Id);
        dto3.setLanguageId(languageId);
        dto3.setChallengeId(challengeId);
        dto3.setScore(score);
        dto3.setSolutionText(solutionText);
        assertThat(dto1.toString()).isNotEmpty();
        assertThat(dto3.toString()).contains(user3Id);
        assertThat(dto3.toString()).contains(languageId);
        assertThat(dto3.toString()).contains(challengeId);
        assertThat(dto3.toString()).contains(String.valueOf(score));
        assertThat(dto3.toString()).contains(solutionText);
        assertThat(dto3.toString()).doesNotContain(userId);
    }
}