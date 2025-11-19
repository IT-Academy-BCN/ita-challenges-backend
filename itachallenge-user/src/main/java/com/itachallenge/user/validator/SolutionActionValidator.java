package com.itachallenge.user.validator;

import com.itachallenge.user.annotations.ValidSolutionAction;

import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SolutionActionValidator implements ConstraintValidator<ValidSolutionAction, String> {
    private static final Set<String> VALID_ACTIONS = Set.of("SAVE", "GIVE_UP", "SUBMIT");

    @Override
    public boolean isValid(String action, ConstraintValidatorContext context){
        return action != null && VALID_ACTIONS.contains(action.toUpperCase());

    }
}
