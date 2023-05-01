package com.itachallenge.challenge.dto.challengessection;

import com.itachallenge.challenge.model.SortingOptions;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SortInfoDto {

    private List<String> options;

    private SortInfoDto() {
        //for tests when deserialization
    }

    private SortInfoDto(List<String> options) {
        this.options = options;
    }

    public static SortInfoDto withAllOptions(){
        return new SortInfoDto(SortingOptions.getAllValues());
    }

    public int getCount(){
        return options.size();
    }
}
