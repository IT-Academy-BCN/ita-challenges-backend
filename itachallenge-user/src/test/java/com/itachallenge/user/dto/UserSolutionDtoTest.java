package com.itachallenge.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
class UserSolutionDtoTest {

    private UserSolutionDto userSolutionDto;
    String userID = UUID.randomUUID().toString();
    String challengeId = UUID.randomUUID().toString();
    String languageId = UUID.randomUUID().toString();
    String status = "status";
    String solutionText = "This is my solution";
    UserSolutionDto solutionDto = new UserSolutionDto();

    @BeforeEach
    public void setUp() {
        userSolutionDto = UserSolutionDto.builder()
                .userId("validUserId")
                .challengeId("validChallengeId")
                .languageId("validLanguageId")
                .status("validChallengeStatus")
                .solutionText("Valid solution text")
                .build();
    }

    @Test
    void testLombokGeneratedMethods() {
        UserSolutionDto dto1 = UserSolutionDto.builder().build();

        assertThat(dto1).isNotNull();
        assertThat(dto1.toString()).isNotEmpty();
        assertThat(dto1.getClass()).isEqualTo(UserSolutionDto.class);
    }

    @Test
    void getterUserSolutionDto_test() {
        assertNotNull(userSolutionDto);
        assertEquals("validUserId", userSolutionDto.getUserId());
        assertEquals("validChallengeId", userSolutionDto.getChallengeId());
        assertEquals("validLanguageId", userSolutionDto.getLanguageId());
        assertEquals("Valid solution text", userSolutionDto.getSolutionText());
        assertEquals("validChallengeStatus", userSolutionDto.getStatus());
    }

    @Test
    void noArgsConstructor_GetterAndSetter_UserSolutionDto_test(){
        solutionDto.setUserId(userID);
        solutionDto.setChallengeId(challengeId);
        solutionDto.setLanguageId(languageId);
        solutionDto.setStatus(status);
        solutionDto.setSolutionText(solutionText);

        assertThat(solutionDto.getUserId()).isEqualTo(userID);
        assertThat(solutionDto.getChallengeId()).isEqualTo(challengeId);
        assertThat(solutionDto.getLanguageId()).isEqualTo(languageId);
        assertThat(solutionDto.getStatus()).isEqualTo(status);
        assertThat(solutionDto.getSolutionText()).isEqualTo(solutionText);
    }

    @Test
    void jsonSerialization_test() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userSolutionDto);
        assertTrue(json.contains("\"uuid_user\":\"validUserId\""));
        assertTrue(json.contains("\"uuid_language\":\"validLanguageId\""));
        assertTrue(json.contains("\"uuid_challenge\":\"validChallengeId\""));
        assertTrue(json.contains("\"solution_text\":\"Valid solution text\""));
        assertTrue(json.contains("\"status\":\"validChallengeStatus\""));
    }
    @Test
    void requiredArgsConstructor_userSolutionScoreDto_test(){
        UserSolutionDto userSolutionDto1 = new UserSolutionDto(
                userID, challengeId, languageId, status, solutionText);
        assertThat(userSolutionDto1.getUserId()).isEqualTo(userID);
        assertThat(userSolutionDto1.getChallengeId()).isEqualTo(challengeId);
        assertThat(userSolutionDto1.getLanguageId()).isEqualTo(languageId);
        assertThat(userSolutionDto1.getStatus()).isEqualTo(status);
        assertThat(userSolutionDto1.getSolutionText()).isEqualTo(solutionText);
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