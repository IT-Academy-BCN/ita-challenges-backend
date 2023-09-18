package com.itachallenge.challenge.validator;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlParamsValidator{

    @Pattern(regexp = "^[1-9]+[0-9]*$", message = "Page must be positive integer")
    private String page;

    @Pattern(regexp = "^[1-9]+[0-9]*$", message = "Size must be positive integer")
    private String size;

}

