package com.itachallenge.user.annotations;

import com.itachallenge.user.validators.GenericUUIDValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenericUUIDValidator.class)
public @interface GenericUUIDValid {
    String message() default "UUID is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String pattern() default ""; // Optional
}
