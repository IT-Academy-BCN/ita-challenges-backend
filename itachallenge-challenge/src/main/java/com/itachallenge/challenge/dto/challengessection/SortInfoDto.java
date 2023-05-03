package com.itachallenge.challenge.dto.challengessection;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SortInfoDto {

    private List<String> options;

    public SortInfoDto() {
        //for tests when deserialization
    }

    public int getCount(){
        return options.size();
    }
}
