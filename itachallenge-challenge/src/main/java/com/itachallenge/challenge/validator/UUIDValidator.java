package com.itachallenge.challenge.validator;

import com.itachallenge.challenge.annotations.ValidUUID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class UUIDValidator implements ConstraintValidator<ValidUUID, UUID> {

    @Override
    public boolean isValid(UUID uuid, ConstraintValidatorContext context) {
        return uuid != null;
    }
}