package com.itachallenge.user.validators;

import com.itachallenge.user.annotations.ValidMongoBDUUID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class MongoDBUUIDValidator implements ConstraintValidator<ValidMongoBDUUID, String> {
    private static final Pattern MONGODB_UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");


    @Override
    public void initialize(ValidMongoBDUUID constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return MONGODB_UUID_PATTERN.matcher(value).matches();
    }
}
