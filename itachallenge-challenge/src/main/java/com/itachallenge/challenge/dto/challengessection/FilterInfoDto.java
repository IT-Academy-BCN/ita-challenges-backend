package com.itachallenge.challenge.dto.challengessection;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterInfoDto {

    private String filterName;

    private List<String> options;

    private boolean uniqueOption;

    private List<String> visibility;

    public FilterInfoDto() {
        //for tests when deserialization
    }

    public int getCount(){
        return options.size();
    }
}
