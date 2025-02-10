package com.itachallenge.user.annotations;

import com.itachallenge.user.helper.GithubUsernameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GithubUsernameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGithubUsername {
    String message() default "Invalid GitHub username format. Must be 1-39 characters long, alphanumeric or hyphen, and cannot start/end with a hyphen.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

