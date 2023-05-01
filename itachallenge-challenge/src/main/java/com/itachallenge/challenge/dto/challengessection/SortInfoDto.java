package com.itachallenge.challenge.dto.challengessection;

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
        return new SortInfoDto(List.of("Popularidad","Fecha")); //TODO: get from enum
    }

    public int getCount(){
        return options.size();
    }
}
