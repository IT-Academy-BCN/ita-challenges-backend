package com.itachallenge.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.user.document.enums.SolutionAction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
class UserSolutionRequestDtoTest {

    private UserSolutionRequestDto userSolutionRequestDto;
    String userID = UUID.randomUUID().toString();
    String challengeId = UUID.randomUUID().toString();
    String languageId = UUID.randomUUID().toString();
    SolutionAction action = SolutionAction.SAVE;
    String solutionText = "This is my solution";
    UserSolutionRequestDto solutionDto = new UserSolutionRequestDto();

    @BeforeEach
    void setUp() {
        userSolutionRequestDto = UserSolutionRequestDto.builder()
                .userId("validUserId")
                .challengeId("validChallengeId")
                .languageId("validLanguageId")
                .action(SolutionAction.SAVE)
                .solutionText("Valid solution text")
                .build();
    }

    @Test
    void testLombokGeneratedMethods() {
        UserSolutionRequestDto dto1 = UserSolutionRequestDto.builder().build();

        assertThat(dto1).isNotNull();
        assertThat(dto1.toString()).isNotEmpty();
        assertThat(dto1.getClass()).isEqualTo(UserSolutionRequestDto.class);
    }

    @Test
    void getterUserSolutionDto_test() {
        assertNotNull(userSolutionRequestDto);
        assertEquals("validUserId", userSolutionRequestDto.getUserId());
        assertEquals("validChallengeId", userSolutionRequestDto.getChallengeId());
        assertEquals("validLanguageId", userSolutionRequestDto.getLanguageId());
        assertEquals("Valid solution text", userSolutionRequestDto.getSolutionText());
        assertEquals(SolutionAction.SAVE, userSolutionRequestDto.getAction());
    }

    @Test
    void noArgsConstructor_GetterAndSetter_UserSolutionDto_test(){
        solutionDto.setUserId(userID);
        solutionDto.setChallengeId(challengeId);
        solutionDto.setLanguageId(languageId);
        solutionDto.setAction(SolutionAction.SAVE);
        solutionDto.setSolutionText(solutionText);

        assertThat(solutionDto.getUserId()).isEqualTo(userID);
        assertThat(solutionDto.getChallengeId()).isEqualTo(challengeId);
        assertThat(solutionDto.getLanguageId()).isEqualTo(languageId);
        assertThat(solutionDto.getAction()).isEqualTo(action);
        assertThat(solutionDto.getSolutionText()).isEqualTo(solutionText);
    }

    @Test
    void jsonSerialization_test() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userSolutionRequestDto);
        assertTrue(json.contains("\"uuid_user\":\"validUserId\""));
        assertTrue(json.contains("\"uuid_language\":\"validLanguageId\""));
        assertTrue(json.contains("\"uuid_challenge\":\"validChallengeId\""));
        assertTrue(json.contains("\"solution_text\":\"Valid solution text\""));
        assertTrue(json.contains("\"action\":\"SAVE\""));
    }
    @Test
    void requiredArgsConstructor_userSolutionScoreDto_test(){
        UserSolutionRequestDto userSolutionRequestDto1 = new UserSolutionRequestDto(
                userID, challengeId, languageId, SolutionAction.SAVE, solutionText);
        assertThat(userSolutionRequestDto1.getUserId()).isEqualTo(userID);
        assertThat(userSolutionRequestDto1.getChallengeId()).isEqualTo(challengeId);
        assertThat(userSolutionRequestDto1.getLanguageId()).isEqualTo(languageId);
        assertThat(userSolutionRequestDto1.getAction()).isEqualTo(action);
        assertThat(userSolutionRequestDto1.getSolutionText()).isEqualTo(solutionText);
    }

    @Test
    void testInvalidUserId() {
        userSolutionRequestDto.setUserId("invalidUserId");
        assertEquals("invalidUserId", userSolutionRequestDto.getUserId());
    }

    @Test
    void testInvalidSolutionText() {
        userSolutionRequestDto.setSolutionText("");
        assertEquals("", userSolutionRequestDto.getSolutionText());
    }
}