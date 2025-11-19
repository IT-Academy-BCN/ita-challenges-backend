package com.itachallenge.user.validator;

import com.itachallenge.user.annotations.ValidSolutionAction;
import com.itachallenge.user.document.enums.SolutionAction;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SolutionActionValidator implements ConstraintValidator<ValidSolutionAction, String> {
    private static final Set<String> VALID_ACTIONS =
            Arrays.stream(SolutionAction.values())
                    .map(Enum::name)
                    .collect(Collectors.toUnmodifiableSet());

    private static final String ERROR_MESSAGE = "Action must be one of: " + String.join(", ", VALID_ACTIONS);

    @Override
    public boolean isValid(String action, ConstraintValidatorContext context){
        if (action == null || !VALID_ACTIONS.contains(action.toUpperCase())) {

            if (context != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(ERROR_MESSAGE)
                        .addConstraintViolation();
            }

            return false;
        }

        return true;
    }
}
