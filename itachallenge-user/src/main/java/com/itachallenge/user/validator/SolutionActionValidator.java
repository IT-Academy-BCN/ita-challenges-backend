package com.itachallenge.user.validator;

import com.itachallenge.user.annotations.ValidSolutionAction;
import com.itachallenge.user.document.enums.SolutionAction;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SolutionActionValidator implements ConstraintValidator<ValidSolutionAction, String> {
    private static final Set<String> VALID_ACTIONS =
            Arrays.stream(SolutionAction.values())
                    .map(Enum::name)
                    .collect(Collectors.toSet());

    @Override
    public boolean isValid(String action, ConstraintValidatorContext context){
        return action != null && VALID_ACTIONS.contains(action.toUpperCase());

    }
}
