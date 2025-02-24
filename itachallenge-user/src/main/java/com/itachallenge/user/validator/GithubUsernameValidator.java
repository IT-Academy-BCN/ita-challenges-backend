package com.itachallenge.user.validator;

import com.itachallenge.user.annotations.ValidGithubUsername;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GithubUsernameValidator implements ConstraintValidator<ValidGithubUsername, String> {

    private static final String GITHUB_USERNAME_REGEX = "^(?!-)[a-zA-Z0-9-]{1,39}(?<!-)$";

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return username != null && username.matches(GITHUB_USERNAME_REGEX);
    }
}

