package com.itachallenge.challenge.validator;

import com.itachallenge.challenge.annotations.ValidGenericPattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class GenericPatternValidator implements ConstraintValidator<ValidGenericPattern, String> {
    @Value("${validation.pageSize}")
    private String patternValue;
    private Pattern pattern;


    @Override
    public void initialize(ValidGenericPattern constraintAnnotation) {
        this.pattern = Pattern.compile(patternValue);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String customMessage = context.getDefaultConstraintMessageTemplate();

        if (!pattern.matcher(value).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate( customMessage )
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
