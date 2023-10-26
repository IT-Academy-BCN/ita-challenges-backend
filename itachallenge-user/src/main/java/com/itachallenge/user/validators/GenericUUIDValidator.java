package com.itachallenge.user.validators;

import com.itachallenge.user.annotations.GenericUUIDValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class GenericUUIDValidator implements ConstraintValidator<GenericUUIDValid, String> {
    @Value("${validation.mongodb_pattern}")
    private String uuidPattern;
    private Pattern UUID_PATTERN;

    @Override
    public void initialize(GenericUUIDValid constraintAnnotation) {
        this.UUID_PATTERN = Pattern.compile(uuidPattern);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String customMessage = context.getDefaultConstraintMessageTemplate();

        if (!UUID_PATTERN.matcher(value).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(customMessage + ": " + value)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
