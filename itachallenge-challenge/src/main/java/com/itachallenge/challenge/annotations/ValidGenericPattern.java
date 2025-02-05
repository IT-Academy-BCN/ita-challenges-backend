package com.itachallenge.challenge.annotations;


import com.itachallenge.challenge.validator.GenericPatternValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenericPatternValidator.class)
public @interface ValidGenericPattern {
    String message() default "The value is invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String pattern() default ""; // Optional
}