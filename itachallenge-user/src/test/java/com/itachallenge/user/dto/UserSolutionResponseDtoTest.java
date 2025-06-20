package com.itachallenge.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.user.document.enums.ChallengeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserSolutionResponseDtoTest {

    private UserSolutionResponseDto userSolutionResponseDto;
    String userId = UUID.randomUUID().toString();
    String challengeId = UUID.randomUUID().toString();
    String languageId = UUID.randomUUID().toString();
    String solutionText = "This is my solution";
    String status = ChallengeStatus.IN_PROGRESS.name();
    UserSolutionResponseDto solutionScoreDto = new UserSolutionResponseDto();
    UserSolutionResponseDto dto1 = UserSolutionResponseDto.builder().build();

    @BeforeEach
    public void setUp() {
        userSolutionResponseDto = UserSolutionResponseDto.builder()
                .userId("validUserId")
                .challengeId("validChallengeId")
                .languageId("validLanguageId")
                .solutionText("Valid solution text")
                .build();
    }

    @Test
    void lombokGeneratedMethods_test() {
        assertThat(dto1).isNotNull();
        assertThat(dto1.getClass()).isEqualTo(UserSolutionResponseDto.class);
    }

    @Test
    void getterUserSolutionScoreDto_test() {
        assertNotNull(userSolutionResponseDto);
        assertEquals("validUserId", userSolutionResponseDto.getUserId());
        assertEquals("validChallengeId", userSolutionResponseDto.getChallengeId());
        assertEquals("validLanguageId", userSolutionResponseDto.getLanguageId());
        assertEquals("Valid solution text", userSolutionResponseDto.getSolutionText());
    }

    @Test
    void noArgsConstructor_GetterAndSetter_UserSolutionScoreDto_test(){
        solutionScoreDto.setUserId(userId);
        solutionScoreDto.setChallengeId(challengeId);
        solutionScoreDto.setLanguageId(languageId);
        solutionScoreDto.setSolutionText(solutionText);

        assertThat(solutionScoreDto.getUserId()).isEqualTo(userId);
        assertThat(solutionScoreDto.getChallengeId()).isEqualTo(challengeId);
        assertThat(solutionScoreDto.getLanguageId()).isEqualTo(languageId);
        assertThat(solutionScoreDto.getSolutionText()).isEqualTo(solutionText);
    }

    @Test
    void jsonSerialization_test() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userSolutionResponseDto);
        assertTrue(json.contains("\"uuid_user\":\"validUserId\""));
        assertTrue(json.contains("\"uuid_language\":\"validLanguageId\""));
        assertTrue(json.contains("\"uuid_challenge\":\"validChallengeId\""));
        assertTrue(json.contains("\"solution_text\":\"Valid solution text\""));
    }

    @Test
    void requiredArgsConstructor_userSolutionScoreDto_test(){
        UserSolutionResponseDto userSolutionResponseDto1 = new UserSolutionResponseDto(
                userId, challengeId, languageId, solutionText,status);
        assertThat(userSolutionResponseDto1.getUserId()).isEqualTo(userId);
        assertThat(userSolutionResponseDto1.getChallengeId()).isEqualTo(challengeId);
        assertThat(userSolutionResponseDto1.getLanguageId()).isEqualTo(languageId);
        assertThat(userSolutionResponseDto1.getSolutionText()).isEqualTo(solutionText);
        assertThat(userSolutionResponseDto1.getStatus()).isEqualTo(status);
    }
}