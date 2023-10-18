package com.itachallenge.challenge.validator;

import com.itachallenge.challenge.annotations.ValidGenericPattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class GenericPatternValidator implements ConstraintValidator<ValidGenericPattern, String> {
    @Value("${validation.number}")
    private String defaultPattern;
    private Pattern pattern;
    private String customMessage;


    @Override
    public void initialize(ValidGenericPattern constraintAnnotation) {
        this.pattern = Pattern.compile(constraintAnnotation.pattern().isEmpty() ? defaultPattern : constraintAnnotation.pattern());
        this.customMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!pattern.matcher(value).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate( customMessage )
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
