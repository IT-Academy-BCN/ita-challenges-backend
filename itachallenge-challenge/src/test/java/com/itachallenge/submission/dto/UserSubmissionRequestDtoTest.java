package com.itachallenge.submission.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserSubmissionRequestDtoTest {

    private static Validator validator;
    String userID = UUID.randomUUID().toString();
    String challengeId = UUID.randomUUID().toString();
    String languageId = UUID.randomUUID().toString();
    String action = "action";
    String submissionText = "This is my submission";
    UserSubmissionRequestDto userSubmissionRequestDto = new UserSubmissionRequestDto();


    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenAllFieldsAreValid() {
        UserSubmissionRequestDto dto = UserSubmissionRequestDto.builder()
                .userId(UUID.randomUUID().toString())
                .challengeId(UUID.randomUUID().toString())
                .languageId(UUID.randomUUID().toString())
                .action("GIVE_UP")
                .submissionText("my submission text")
                .build();

        Set<ConstraintViolation<UserSubmissionRequestDto>> violations =
                validator.validate(dto);

        assertTrue(violations.isEmpty(), "DTO should be valid");
    }

    @Test
    void shouldFailValidation_whenUserIdIsInvalidUUID() {
        UserSubmissionRequestDto dto = UserSubmissionRequestDto.builder()
                .userId("not-a-uuid")
                .challengeId(UUID.randomUUID().toString())
                .languageId(UUID.randomUUID().toString())
                .action("ANY")
                .submissionText("text")
                .build();

        Set<ConstraintViolation<UserSubmissionRequestDto>> violations =
                validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().contains("Invalid UUID")),
                "Expected Invalid UUID error"
        );
    }

    @Test
    void shouldFailValidation_whenChallengeIdIsInvalidUUID() {
        UserSubmissionRequestDto dto = UserSubmissionRequestDto.builder()
                .userId(UUID.randomUUID().toString())
                .challengeId("1234")
                .languageId(UUID.randomUUID().toString())
                .action("ANY")
                .submissionText("text")
                .build();

        Set<ConstraintViolation<UserSubmissionRequestDto>> violations =
                validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("challengeId")),
                "Expected challengeId validation error"
        );
    }

    @Test
    void shouldFailValidation_whenLanguageIdIsInvalidUUID() {
        UserSubmissionRequestDto dto = UserSubmissionRequestDto.builder()
                .userId(UUID.randomUUID().toString())
                .challengeId(UUID.randomUUID().toString())
                .languageId("xxx")
                .action("ANY")
                .submissionText("text")
                .build();

        Set<ConstraintViolation<UserSubmissionRequestDto>> violations =
                validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("languageId")),
                "Expected languageId validation error"
        );
    }

    @Test
    void shouldFailValidation_whenSolutionTextIsBlank() {
        UserSubmissionRequestDto dto = UserSubmissionRequestDto.builder()
                .userId(UUID.randomUUID().toString())
                .challengeId(UUID.randomUUID().toString())
                .languageId(UUID.randomUUID().toString())
                .action("ANY")
                .submissionText("   ")
                .build();

        Set<ConstraintViolation<UserSubmissionRequestDto>> violations =
                validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().contains("Submission text is required")),
                "Expected NotBlank validation error"
        );
    }

    @BeforeEach
    public void setUp() {
        userSubmissionRequestDto = UserSubmissionRequestDto.builder()
                .userId("validUserId")
                .challengeId("validChallengeId")
                .languageId("validLanguageId")
                .action("validChallengeAction")
                .submissionText("Valid submission text")
                .build();
    }

    @Test
    void testLombokGeneratedMethods() {
        UserSubmissionRequestDto dto1 = UserSubmissionRequestDto.builder().build();

        assertThat(dto1).isNotNull();
        assertThat(dto1.toString()).isNotEmpty();
        assertThat(dto1.getClass()).isEqualTo(UserSubmissionRequestDto.class);
    }

    @Test
    void getterUserSubmissionDto_test() {
        assertNotNull(userSubmissionRequestDto);
        assertEquals("validUserId", userSubmissionRequestDto.getUserId());
        assertEquals("validChallengeId", userSubmissionRequestDto.getChallengeId());
        assertEquals("validLanguageId", userSubmissionRequestDto.getLanguageId());
        assertEquals("Valid submission text", userSubmissionRequestDto.getSubmissionText());
        assertEquals("validChallengeAction", userSubmissionRequestDto.getAction());
    }

    @Test
    void noArgsConstructor_GetterAndSetter_UserSubmissionDto_test(){
        userSubmissionRequestDto.setUserId(userID);
        userSubmissionRequestDto.setChallengeId(challengeId);
        userSubmissionRequestDto.setLanguageId(languageId);
        userSubmissionRequestDto.setAction(action);
        userSubmissionRequestDto.setSubmissionText(submissionText);

        assertThat(userSubmissionRequestDto.getUserId()).isEqualTo(userID);
        assertThat(userSubmissionRequestDto.getChallengeId()).isEqualTo(challengeId);
        assertThat(userSubmissionRequestDto.getLanguageId()).isEqualTo(languageId);
        assertThat(userSubmissionRequestDto.getAction()).isEqualTo(action);
        assertThat(userSubmissionRequestDto.getSubmissionText()).isEqualTo(submissionText);
    }

    @Test
    void jsonSerialization_test() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userSubmissionRequestDto);
        assertTrue(json.contains("\"uuid_user\":\"validUserId\""));
        assertTrue(json.contains("\"uuid_language\":\"validLanguageId\""));
        assertTrue(json.contains("\"uuid_challenge\":\"validChallengeId\""));
        assertTrue(json.contains("\"submission_text\":\"Valid submission text\""));
        assertTrue(json.contains("\"action\":\"validChallengeAction\""));
    }
    @Test
    void requiredArgsConstructor_userSubmissionScoreDto_test(){
        UserSubmissionRequestDto userSolutionRequestDto = new UserSubmissionRequestDto(
                userID, challengeId, languageId, action, submissionText);
        assertThat(userSolutionRequestDto.getUserId()).isEqualTo(userID);
        assertThat(userSolutionRequestDto.getChallengeId()).isEqualTo(challengeId);
        assertThat(userSolutionRequestDto.getLanguageId()).isEqualTo(languageId);
        assertThat(userSolutionRequestDto.getAction()).isEqualTo(action);
        assertThat(userSolutionRequestDto.getSubmissionText()).isEqualTo(submissionText);
    }

    @Test
    void testInvalidUserId() {
        userSubmissionRequestDto.setUserId("invalidUserId");
        assertEquals("invalidUserId", userSubmissionRequestDto.getUserId());
    }

    @Test
    void testInvalidSolutionText() {
        userSubmissionRequestDto.setSubmissionText("");
        assertEquals("", userSubmissionRequestDto.getSubmissionText());
    }
}

