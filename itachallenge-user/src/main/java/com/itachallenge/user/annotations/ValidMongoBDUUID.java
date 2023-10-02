package com.itachallenge.user.annotations;

import com.itachallenge.user.validators.MongoDBUUIDValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MongoDBUUIDValidator.class)
public @interface ValidMongoBDUUID {
    String message() default "The MongoDB UUID is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
