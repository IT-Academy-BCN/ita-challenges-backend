package com.itachallenge.challenge.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Setter;

@Setter
public class PagingParametersDto {

    @Pattern(regexp = "^[1-9]+$", message = "pageNumber must be positive integer")
    private String pageNumber;

    @Pattern(regexp = "^[1-9]+$", message = "pageSize must be positive integer")
    private String pageSize;

}

