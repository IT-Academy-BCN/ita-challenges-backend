package com.itachallenge.challenge.helper;

import jakarta.validation.constraints.Pattern;

public class ParameterValidator {

    @Pattern(regexp="^[0-9]+$", message="the value must be positive integer")
    private String pageNumber;

    @Pattern(regexp="^[0-9]+$", message="The value must be positive integer")
    private String pageSize;

}
