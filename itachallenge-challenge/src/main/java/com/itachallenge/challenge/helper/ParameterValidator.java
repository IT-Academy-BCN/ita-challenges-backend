package com.itachallenge.challenge.helper;

import jakarta.validation.constraints.Pattern;

public class ParameterValidator {

    @lombok.Setter
    @Pattern(regexp = "^[1-9]+$", message = "pageNumber must be positive integer")
    private String pageNumber;

    @lombok.Setter
    @Pattern(regexp = "^[1-9]+$", message = "pageSize must be positive integer")
    private String pageSize;

}