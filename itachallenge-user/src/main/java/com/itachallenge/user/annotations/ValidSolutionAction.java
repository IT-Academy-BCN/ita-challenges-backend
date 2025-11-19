package com.itachallenge.user.annotations;

import com.itachallenge.user.validator.SolutionActionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SolutionActionValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSolutionAction {
    String message() default "Invalid action";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
