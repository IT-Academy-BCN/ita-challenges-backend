package com.itachallenge.challenge.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

//@Component
@Getter
@Setter
public class PagingParametersDto {

    @Pattern(regexp = "^[1-9]+$", message = "pageNumber must be positive integer")
    private String pageNumber;

    @Pattern(regexp = "^[1-9]+$", message = "pageSize must be positive integer")
    private String pageSize;

    //private int count;

    //private T[] results;

/*    public PagingParametersDto() {}

    public void setInfo(String pageNumber, String pageSize,T[] results) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        //this.count = count;
        this.results = results;
    }*/

}

