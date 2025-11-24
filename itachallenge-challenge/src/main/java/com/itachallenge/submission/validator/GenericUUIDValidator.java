package com.itachallenge.submission.validator;

import com.itachallenge.submission.annotations.GenericUUIDValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;
import java.util.Objects;

public class GenericUUIDValidator implements ConstraintValidator<GenericUUIDValid, String> {

    private static final String DEFAULT_UUID_PATTERN =
            "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[1-5][0-9a-fA-F]{3}\\-[89abAB][0-9a-fA-F]{3}\\-[0-9a-fA-F]{12}$";

    private Pattern uuidPattern;

    @Override
    public void initialize(GenericUUIDValid constraintAnnotation) {
        String patternFromAnnotation = constraintAnnotation.pattern();
        String patternToUse = (patternFromAnnotation != null && !patternFromAnnotation.trim().isEmpty())
                ? patternFromAnnotation
                : DEFAULT_UUID_PATTERN;
        this.uuidPattern = Pattern.compile(patternToUse);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String customMessage = context.getDefaultConstraintMessageTemplate();

        if (value == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(customMessage + ": value is null")
                    .addConstraintViolation();
            return false;
        }

        if (!uuidPattern.matcher(value).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(customMessage + ": " + value)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

