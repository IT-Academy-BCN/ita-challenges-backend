package com.itachallenge.challenge.dto;

import com.itachallenge.challenge.enums.Topic;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ChallengeCreateDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        Locale.setDefault(Locale.ENGLISH);
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(
                        new ResourceBundleMessageInterpolator(
                                new PlatformResourceBundleLocator("messages")
                        )
                )
                .buildValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should fail validation when required string fields are empty")
    void shouldFailWhenStringFieldsEmpty() {
        ChallengeCreateDto dto = ChallengeCreateDto.builder()
                .challengeTitle("")
                .description("")
                .language("")
                .solution("")
                .topic(null)
                .tags(Collections.emptyList())
                .build();

        Set<ConstraintViolation<ChallengeCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty()
                .anyMatch(v -> v.getMessage().equals("The challenge title cannot be empty."))
                .anyMatch(v -> v.getMessage().equals("The description cannot be empty."))
                .anyMatch(v -> v.getMessage().equals("The language field cannot be empty."))
                .anyMatch(v -> v.getMessage().equals("The solution text cannot be empty."))
                .anyMatch(v -> v.getMessage().equals("The topic must be provided."))
                .anyMatch(v -> v.getMessage().equals("At least one tag must be provided for the challenge."));
    }

    @Test
    @DisplayName("Should fail validation when topic is null")
    void shouldFailWhenTopicIsNull() {
        ChallengeCreateDto dto = ChallengeCreateDto.builder()
                .challengeTitle("Challenge 1")
                .description("Description")
                .language("Java")
                .solution("System.out.println(\"Hello\");")
                .topic(null)
                .tags(List.of(UUID.randomUUID()))
                .build();

        Set<ConstraintViolation<ChallengeCreateDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getMessage().equals("The topic must be provided."));
    }

    @Test
    @DisplayName("Should fail validation when tags list is empty")
    void shouldFailWhenTagsListEmpty() {
        ChallengeCreateDto dto = ChallengeCreateDto.builder()
                .challengeTitle("Challenge 1")
                .description("Description")
                .language("Java")
                .solution("System.out.println(\"Hello\");")
                .topic(com.itachallenge.challenge.enums.Topic.DEBUGGING)
                .tags(Collections.emptyList())
                .build();

        Set<ConstraintViolation<ChallengeCreateDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getMessage().equals("At least one tag must be provided for the challenge."));
    }

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void shouldPassWhenValid() {
        ChallengeCreateDto dto = ChallengeCreateDto.builder()
                .challengeTitle("Valid Challenge")
                .description("A proper description")
                .language("Java")
                .solution("System.out.println(\"Hello World\");")
                .topic(Topic.DEBUGGING)
                .tags(List.of(UUID.randomUUID()))
                .build();

        Set<ConstraintViolation<ChallengeCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
