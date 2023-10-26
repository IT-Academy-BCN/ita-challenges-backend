package com.itachallenge.challenge.validator;

import com.itachallenge.challenge.annotations.ValidGenericPattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class GenericPatternValidator implements ConstraintValidator<ValidGenericPattern, String> {
    private static final String DEFAULT_PATTERN = "^\\d{1,8}$";
    private Pattern pattern;

    @Override
    public void initialize(ValidGenericPattern constraintAnnotation) {
        this.pattern = Pattern.compile(constraintAnnotation.pattern().isEmpty() ? DEFAULT_PATTERN : constraintAnnotation.pattern());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return pattern.matcher(value).matches();
    }
}