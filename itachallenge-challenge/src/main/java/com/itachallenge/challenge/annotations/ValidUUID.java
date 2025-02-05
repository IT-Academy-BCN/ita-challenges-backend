package com.itachallenge.challenge.annotations;

import com.itachallenge.challenge.validator.UUIDValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UUIDValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUUID {
    String message() default "Invalid UUID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}