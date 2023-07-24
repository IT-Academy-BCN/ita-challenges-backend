package com.itachallenge.challenge.helper;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.Pattern;

import java.util.Optional;

public class ParameterValidator {

    private static final java.util.regex.Pattern POSITIVE_INTEGER_FORM = java.util.regex.Pattern.compile("^[1-9]+$");

    @Pattern(regexp = "^[1-9]+$", message = "pageNumber must be positive integer")
    private String pageNumber;

    @Pattern(regexp = "^[1-9]+$", message = "pageSize must be positive integer")
    private String pageSize;

    public Integer getValidPageNumber(String pageNumber) {
        boolean validPageNumber = !StringUtils.isEmpty(pageNumber) && POSITIVE_INTEGER_FORM.matcher(pageNumber).matches();
        if (validPageNumber) {
            return Integer.parseInt(pageNumber);
        }
        return null;
    }

/*    public Optional<Integer> getValidPageSize(String pageSize) {
        boolean validPageSize =!StringUtils.isEmpty(pageSize) && POSITIVE_INTEGER_FORM.matcher(pageSize).matches();
        if (validPageSize) {
            return Optional.of(Integer.parseInt(pageSize));
        }
        return Optional.empty();
    }*/

    public Integer getValidPageSize(String pageSize) {
        boolean validPageSize =!StringUtils.isEmpty(pageSize) && POSITIVE_INTEGER_FORM.matcher(pageSize).matches();
        if (validPageSize) {
            return Integer.parseInt(pageSize);
        }
        return null;
    }
}
